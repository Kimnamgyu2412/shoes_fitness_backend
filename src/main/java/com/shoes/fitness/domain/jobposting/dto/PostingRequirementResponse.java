package com.shoes.fitness.domain.jobposting.dto;

import com.shoes.fitness.entity.FitnessRequirementMaster;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostingRequirementResponse {
    private String requirementCode;
    private String requirementName;
    private String category;

    public static PostingRequirementResponse from(FitnessRequirementMaster master) {
        return PostingRequirementResponse.builder()
                .requirementCode(master.getRequirementCode())
                .requirementName(master.getRequirementName())
                .category(master.getCategory())
                .build();
    }
}
