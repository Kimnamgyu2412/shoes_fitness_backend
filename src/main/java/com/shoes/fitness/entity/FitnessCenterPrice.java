package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_center_price", indexes = {
        @Index(name = "idx_price_center_id", columnList = "center_id"),
        @Index(name = "idx_price_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessCenterPrice {

    @Id
    @Column(name = "price_id", length = 32)
    private String priceId;

    @PrePersist
    public void prePersist() {
        if (this.priceId == null) {
            this.priceId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "center_id", length = 32, nullable = false)
    private String centerId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "duration", length = 50)
    private String duration;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
