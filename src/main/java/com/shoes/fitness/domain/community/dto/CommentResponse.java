package com.shoes.fitness.domain.community.dto;

import com.shoes.fitness.entity.CommunityComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private String commentId;
    private String postId;
    private String parentId;
    private String authorId;
    private String authorName;
    private String authorImage;
    private String content;
    private LocalDateTime createdAt;
    @Builder.Default
    private List<CommentResponse> replies = new ArrayList<>();

    public static CommentResponse from(CommunityComment entity) {
        return CommentResponse.builder()
                .commentId(entity.getCommentId())
                .postId(entity.getPostId())
                .parentId(entity.getParentId())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .authorImage(entity.getAuthorImage())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .replies(new ArrayList<>())
                .build();
    }
}
