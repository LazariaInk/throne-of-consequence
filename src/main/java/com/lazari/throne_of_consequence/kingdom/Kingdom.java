package com.lazari.throne_of_consequence.kingdom;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "kingdoms",
        indexes = @Index(name = "idx_kingdom_player_id", columnList = "playerId", unique = true)
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Kingdom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String playerId;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false) private int money;
    @Column(nullable = false) private int reputation;
    @Column(nullable = false) private int religion;
    @Column(nullable = false) private int stability;

    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;

    @PrePersist
    void onCreate() {
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
