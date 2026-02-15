package com.lazari.throne_of_consequence.kingdom;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/kingdom")
public class KingdomController {

    private final KingdomService service;

    public KingdomController(KingdomService service) {
        this.service = service;
    }

    @GetMapping("/state")
    public Map<String, Object> state(@RequestParam String playerId) {
        Kingdom k = service.getOrCreate(playerId);

        return Map.of(
                "playerId", k.getPlayerId(),
                "name", k.getName(),
                "stats", Map.of(
                        "money", k.getMoney(),
                        "reputation", k.getReputation(),
                        "religion", k.getReligion(),
                        "stability", k.getStability()
                )
        );
    }
}
