package com.shoes.fitness.domain.community.dto;

import com.shoes.fitness.entity.CommunityPost;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResponse {
    private String postId;
    private String category;
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    private String authorImage;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isNotice;
    private Long price;
    private String location;
    private String locationDetail;
    private String tradeStatus;
    private String contact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PostImageResponse> images;
    private List<CommentResponse> comments;
    private Boolean isLiked;
    private Boolean isOwner;

    public static PostDetailResponse from(CommunityPost entity, List<PostImageResponse> images,
                                          List<CommentResponse> comments, Boolean isLiked, Boolean isOwner) {
        return PostDetailResponse.builder()
                .postId(entity.getPostId())
                .category(entity.getCategory() != null ? entity.getCategory().name() : null)
                .title(entity.getTitle())
                .content(entity.getContent())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .authorImage(entity.getAuthorImage())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .commentCount(entity.getCommentCount())
                .isNotice(entity.getIsNotice())
                .price(entity.getPrice())
                .location(entity.getLocation())
                .locationDetail(entity.getLocationDetail())
                .tradeStatus(entity.getTradeStatus() != null ? entity.getTradeStatus().name() : null)
                .contact(entity.getContact())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .images(images)
                .comments(comments)
                .isLiked(isLiked)
                .isOwner(isOwner)
                .build();
    }
}
