package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_community_post_image", indexes = {
        @Index(name = "idx_post_id", columnList = "post_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostImage {

    @Id
    @Column(name = "image_id", length = 32)
    private String imageId;

    @PrePersist
    public void prePersist() {
        if (this.imageId == null) {
            this.imageId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "post_id", length = 32, nullable = false)
    private String postId;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
