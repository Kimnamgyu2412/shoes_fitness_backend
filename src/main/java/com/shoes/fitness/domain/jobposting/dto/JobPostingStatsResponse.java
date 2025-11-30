package com.shoes.fitness.domain.jobposting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingStatsResponse {
    private Integer viewCount;
    private Integer applyCount;
}
