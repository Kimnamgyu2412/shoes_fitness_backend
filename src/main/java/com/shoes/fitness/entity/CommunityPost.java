package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_community_post", indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_author_id", columnList = "author_id"),
        @Index(name = "idx_center_id", columnList = "center_id"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_category_created", columnList = "category, created_at"),
        @Index(name = "idx_is_notice", columnList = "is_notice, category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPost {

    @Id
    @Column(name = "post_id", length = 32)
    private String postId;

    @PrePersist
    public void prePersist() {
        if (this.postId == null) {
            this.postId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "center_id", length = 32)
    private String centerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20, nullable = false)
    private PostCategory category;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "author_id", length = 32, nullable = false)
    private String authorId;

    @Column(name = "author_name", length = 50, nullable = false)
    private String authorName;

    @Column(name = "author_image", length = 500)
    private String authorImage;

    @Column(name = "price")
    private Long price;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "location_detail", length = 200)
    private String locationDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_status", length = 20)
    private TradeStatus tradeStatus;

    @Column(name = "contact", length = 50)
    private String contact;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "comment_count")
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "is_notice")
    @Builder.Default
    private Boolean isNotice = false;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PostCategory {
        free, club_sale, club_buy, used
    }

    public enum TradeStatus {
        available, reserved, sold
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public void incrementCommentCount() {
        this.commentCount = (this.commentCount == null ? 0 : this.commentCount) + 1;
    }

    public void decrementCommentCount() {
        this.commentCount = Math.max(0, (this.commentCount == null ? 0 : this.commentCount) - 1);
    }

    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }

    public void decrementLikeCount() {
        this.likeCount = Math.max(0, (this.likeCount == null ? 0 : this.likeCount) - 1);
    }
}
