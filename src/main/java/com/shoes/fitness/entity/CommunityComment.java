package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_community_comment", indexes = {
        @Index(name = "idx_post_id", columnList = "post_id"),
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_author_id", columnList = "author_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityComment {

    @Id
    @Column(name = "comment_id", length = 32)
    private String commentId;

    @PrePersist
    public void prePersist() {
        if (this.commentId == null) {
            this.commentId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "post_id", length = 32, nullable = false)
    private String postId;

    @Column(name = "parent_id", length = 32)
    private String parentId;

    @Column(name = "author_id", length = 32, nullable = false)
    private String authorId;

    @Column(name = "author_name", length = 50, nullable = false)
    private String authorName;

    @Column(name = "author_image", length = 500)
    private String authorImage;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
