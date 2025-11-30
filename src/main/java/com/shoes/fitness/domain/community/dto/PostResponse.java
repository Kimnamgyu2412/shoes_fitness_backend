package com.shoes.fitness.domain.community.dto;

import com.shoes.fitness.entity.CommunityPost;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private String postId;
    private String centerId;
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
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(CommunityPost entity) {
        return PostResponse.builder()
                .postId(entity.getPostId())
                .centerId(entity.getCenterId())
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
                .build();
    }

    public static PostResponse fromWithThumbnail(CommunityPost entity, String thumbnailUrl) {
        PostResponse response = from(entity);
        response.setThumbnailUrl(thumbnailUrl);
        return response;
    }
}
