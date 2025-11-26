package com.shoes.fitness.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_fitness_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshFitnessToken {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "token", length = 255, nullable = false, unique = true)
    private String token;

    @Column(name = "fitness_id", length = 32, nullable = false)
    private String fitnessId;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "absolute_expiry_date", nullable = false)
    private LocalDateTime absoluteExpiryDate;

    @Column(name = "refresh_count", nullable = false)
    @Builder.Default
    private Integer refreshCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_id", referencedColumnName = "fitness_id", insertable = false, updatable = false)
    private FitnessPartner fitnessPartner;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isAbsoluteExpired() {
        return LocalDateTime.now().isAfter(absoluteExpiryDate);
    }

    public void incrementRefreshCount() {
        this.refreshCount++;
    }
}
