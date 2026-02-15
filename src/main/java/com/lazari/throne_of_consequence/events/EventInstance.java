package com.lazari.throne_of_consequence.events;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(
        name = "event_instances",
        indexes = @Index(name = "idx_event_player_created", columnList = "playerId,createdAt")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String playerId;

    @Column(nullable = false, length = 64)
    private String eventKey;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant resolvedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> payload;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> result;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
