package com.lazari.throne_of_consequence.kingdom;

import com.lazari.throne_of_consequence.common.Clamp;
import com.lazari.throne_of_consequence.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KingdomService {

    private final KingdomRepository repo;

    public KingdomService(KingdomRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Kingdom getOrCreate(String playerId) {
        return repo.findByPlayerId(playerId).orElseGet(() -> repo.save(
                Kingdom.builder()
                        .playerId(playerId)
                        .name("Regatul lui " + playerId)
                        .money(50).reputation(50).religion(50).stability(50)
                        .build()
        ));
    }

    @Transactional(readOnly = true)
    public Kingdom get(String playerId) {
        return repo.findByPlayerId(playerId)
                .orElseThrow(() -> new NotFoundException("Kingdom not found for playerId=" + playerId));
    }

    @Transactional
    public Kingdom applyDelta(String playerId, int bani, int reputatie, int credinta, int stabilitate) {
        Kingdom k = getOrCreate(playerId);

        k.setMoney(Clamp.between(k.getMoney() + bani, 0, 100));
        k.setReputation(Clamp.between(k.getReputation() + reputatie, 0, 100));
        k.setReligion(Clamp.between(k.getReligion() + credinta, 0, 100));
        k.setStability(Clamp.between(k.getStability() + stabilitate, 0, 100));

        return repo.save(k);
    }
}
