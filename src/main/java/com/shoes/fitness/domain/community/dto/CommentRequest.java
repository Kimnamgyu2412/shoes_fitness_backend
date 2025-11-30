package com.shoes.fitness.domain.community.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    private String postId;
    private String parentId;
    private String content;
}
