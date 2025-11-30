package com.shoes.fitness.domain.community.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponse {
    private Boolean liked;
    private Integer likeCount;
}
