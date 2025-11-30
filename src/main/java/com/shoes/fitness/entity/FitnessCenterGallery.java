package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_center_gallery", indexes = {
        @Index(name = "idx_gallery_center_id", columnList = "center_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessCenterGallery {

    @Id
    @Column(name = "gallery_id", length = 32)
    private String galleryId;

    @PrePersist
    public void prePersist() {
        if (this.galleryId == null) {
            this.galleryId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "center_id", length = 32, nullable = false)
    private String centerId;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
