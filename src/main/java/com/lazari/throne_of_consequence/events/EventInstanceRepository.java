package com.lazari.throne_of_consequence.events;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventInstanceRepository extends JpaRepository<EventInstance, Long> {
    Optional<EventInstance> findByIdAndPlayerId(Long id, String playerId);
}
