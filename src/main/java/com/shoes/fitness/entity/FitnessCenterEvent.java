package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_center_event", indexes = {
        @Index(name = "idx_event_center_id", columnList = "center_id"),
        @Index(name = "idx_event_status", columnList = "status"),
        @Index(name = "idx_event_date_range", columnList = "start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessCenterEvent {

    @Id
    @Column(name = "event_id", length = 32)
    private String eventId;

    @PrePersist
    public void prePersist() {
        if (this.eventId == null) {
            this.eventId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "center_id", length = 32, nullable = false)
    private String centerId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "discount_rate", length = 50)
    private String discountRate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private EventStatus status = EventStatus.SCHEDULED;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EventStatus {
        SCHEDULED, ACTIVE, ENDED
    }
}
