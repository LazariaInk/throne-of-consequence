package com.lazari.throne_of_consequence.events;

import com.lazari.throne_of_consequence.common.Clamp;
import com.lazari.throne_of_consequence.common.NotFoundException;
import com.lazari.throne_of_consequence.events.dto.ClaimEventResponse;
import com.lazari.throne_of_consequence.events.dto.ReplyResponse;
import com.lazari.throne_of_consequence.kingdom.Kingdom;
import com.lazari.throne_of_consequence.kingdom.KingdomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class EventService {

    private final EventInstanceRepository repo;
    private final KingdomService kingdomService;
    private final Random rnd = new Random();

    public EventService(EventInstanceRepository repo, KingdomService kingdomService) {
        this.repo = repo;
        this.kingdomService = kingdomService;
    }

    @Transactional
    public ClaimEventResponse claim(String playerId) {
        Kingdom k = kingdomService.getOrCreate(playerId);

        String eventKey = pickEventKey(k);
        Map<String, Object> payload = generatePayload(eventKey);

        EventInstance inst = repo.save(EventInstance.builder()
                .playerId(playerId)
                .eventKey(eventKey)
                .status("CLAIMED")
                .payload(payload)
                .build());

        return new ClaimEventResponse(inst.getId(), eventKey, payload);
    }

    @Transactional
    public ReplyResponse reply(String playerId, long eventInstanceId, String reply) {
        EventInstance inst = repo.findByIdAndPlayerId(eventInstanceId, playerId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if ("RESOLVED".equals(inst.getStatus())) {
            throw new IllegalStateException("Event already resolved");
        }

        Map<String, Integer> effects = evaluateSimple(inst.getEventKey(), reply);

        effects.replaceAll((k, v) -> Clamp.between(v, -5, 5));

        var newK = kingdomService.applyDelta(
                playerId,
                effects.getOrDefault("bani", 0),
                effects.getOrDefault("reputatie", 0),
                effects.getOrDefault("credinta", 0),
                effects.getOrDefault("stabilitate", 0)
        );

        String consequence = makeConsequence(inst.getEventKey(), effects);

        inst.setStatus("RESOLVED");
        inst.setResolvedAt(Instant.now());
        inst.setResult(Map.of(
                "effects", effects,
                "consequence", consequence,
                "reply", reply == null ? "" : reply
        ));
        repo.save(inst);

        Map<String, Integer> newStats = Map.of(
                "money", newK.getMoney(),
                "reputation", newK.getReputation(),
                "religion", newK.getReligion(),
                "stability", newK.getStability()
        );

        return new ReplyResponse(eventInstanceId, effects, consequence, newStats);
    }

    private String pickEventKey(Kingdom k) {
        // v1 logic super simplă
        if (k.getMoney() < 35) return "treasury_warning";
        if (k.getStability() < 40) return "bandits_report";
        if (k.getReligion() < 40) return "heresy_rumor";
        return rnd.nextBoolean() ? "peasants_tax" : "merchant_offer";
    }

    private Map<String, Object> generatePayload(String eventKey) {
        return switch (eventKey) {
            case "peasants_tax" -> Map.of(
                    "speaker_name", "Țăranul Albert",
                    "title", "Roada slabă",
                    "text", "Măria Ta, nu putem plăti toate dările anul acesta. Ce hotărăști?",
                    "tags", List.of("taxes", "peasants"),
                    "severity", 3
            );
            case "merchant_offer" -> Map.of(
                    "speaker_name", "Negustorul Darius Valen",
                    "title", "Propunere de comerț",
                    "text", "Vreau o rută comercială prin ținuturile tale. Ofer aur, dar cer siguranță.",
                    "tags", List.of("trade", "court"),
                    "severity", 3
            );
            case "bandits_report" -> Map.of(
                    "speaker_name", "Căpitanul Roderic",
                    "title", "Banditi pe drumuri",
                    "text", "Atacurile cresc. Investim în patrule sau pedeapsă exemplară?",
                    "tags", List.of("crime", "army"),
                    "severity", 4
            );
            case "heresy_rumor" -> Map.of(
                    "speaker_name", "Sora Miriam",
                    "title", "Șoapte de erezie",
                    "text", "Un predicator răspândește vorbe păgâne. Îl închidem sau investigăm?",
                    "tags", List.of("religion", "court"),
                    "severity", 3
            );
            case "treasury_warning" -> Map.of(
                    "speaker_name", "Lord Mathias",
                    "title", "Vistieria slăbește",
                    "text", "Rezervele scad. Creștem taxe, împrumutăm, sau tăiem cheltuieli?",
                    "tags", List.of("economy", "court"),
                    "severity", 4
            );
            default -> Map.of(
                    "speaker_name", "Scribul",
                    "title", "Zi obișnuită",
                    "text", "O cerere minoră ajunge la tine. Ce hotărăști?",
                    "tags", List.of("court"),
                    "severity", 1
            );
        };
    }

    private Map<String, Integer> evaluateSimple(String eventKey, String reply) {
        String r = reply == null ? "" : reply.trim().toLowerCase();

        boolean tooShort = r.length() < 12;
        boolean offTopic = r.contains("meci") || r.contains("lol") || r.contains("nu-mi pasă") || r.contains("youtube");

        if (tooShort || offTopic) {
            return new HashMap<>(Map.of(
                    "money", -1,
                    "reputation", -4,
                    "religion", -2,
                    "stability", -2
            ));
        }

        return switch (eventKey) {
            case "peasants_tax" -> new HashMap<>(Map.of("money", -2, "reputation", 3, "religion", 1, "stability", 2));
            case "merchant_offer" -> new HashMap<>(Map.of("money", 2, "reputation", 1, "religion", 0, "stability", -1));
            case "bandits_report" -> new HashMap<>(Map.of("money", -3, "reputation", 1, "religion", 0, "stability", 3));
            case "heresy_rumor" -> new HashMap<>(Map.of("money", 0, "reputation", 1, "religion", 2, "stability", 1));
            case "treasury_warning" -> new HashMap<>(Map.of("money", 3, "reputation", -1, "religion", 0, "stability", -1));
            default -> new HashMap<>(Map.of("money", 0, "reputation", 0, "religion", 0, "stability", 0));
        };
    }

    private String makeConsequence(String eventKey, Map<String, Integer> effects) {
        if (effects.getOrDefault("reputation", 0) <= -4) {
            return "Curtea murmură: regele pare ieșit din fire, iar oamenii își pierd încrederea.";
        }

        return switch (eventKey) {
            case "peasants_tax" -> "Satele răsuflă ușurate, dar vistieria simte lovitura.";
            case "merchant_offer" -> "Negustorii testează piața. Unii sunt încântați, alții prudenți.";
            case "bandits_report" -> "Patrulele se mișcă mai des; drumurile par mai sigure.";
            case "heresy_rumor" -> "Clerul își notează decizia ta. Unii aprobă, alții șoptesc.";
            case "treasury_warning" -> "Vistiernicul bifează măsuri. Curtea devine mai austeră.";
            default -> "Ziua trece fără valuri mari.";
        };
    }
}
