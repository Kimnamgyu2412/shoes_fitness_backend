package com.shoes.fitness.domain.jobposting.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingListResponse {
    private List<JobPostingResponse> postings;
    private Long totalCount;
}
