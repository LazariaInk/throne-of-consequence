package com.lazari.throne_of_consequence.kingdom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KingdomRepository extends JpaRepository<Kingdom, Long> {
    Optional<Kingdom> findByPlayerId(String playerId);
}
