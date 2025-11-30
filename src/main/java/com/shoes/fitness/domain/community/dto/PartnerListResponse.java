package com.shoes.fitness.domain.community.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerListResponse {
    private List<PartnerResponse> partners;
    private long totalCount;
}
