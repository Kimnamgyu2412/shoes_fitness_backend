package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_center_facility",
       uniqueConstraints = @UniqueConstraint(name = "uk_center_facility", columnNames = {"center_id", "facility_code"}),
       indexes = @Index(name = "idx_facility_center_id", columnList = "center_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessCenterFacility {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "center_id", length = 32, nullable = false)
    private String centerId;

    @Column(name = "facility_code", length = 20, nullable = false)
    private String facilityCode;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
