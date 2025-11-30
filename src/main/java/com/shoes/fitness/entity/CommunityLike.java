package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_community_like",
        uniqueConstraints = @UniqueConstraint(name = "idx_post_user", columnNames = {"post_id", "user_id"}),
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityLike {

    @Id
    @Column(name = "like_id", length = 32)
    private String likeId;

    @PrePersist
    public void prePersist() {
        if (this.likeId == null) {
            this.likeId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "post_id", length = 32, nullable = false)
    private String postId;

    @Column(name = "user_id", length = 32, nullable = false)
    private String userId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
