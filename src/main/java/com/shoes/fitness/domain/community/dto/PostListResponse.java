package com.shoes.fitness.domain.community.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponse {
    private List<PostResponse> posts;
    private long totalCount;
    private int totalPages;
    private int currentPage;
}
