package com.shoes.fitness.domain.jobposting.dto;

import com.shoes.fitness.entity.FitnessRequirementMaster;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequirementMasterResponse {
    private String requirementCode;
    private String requirementName;
    private String category;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static RequirementMasterResponse from(FitnessRequirementMaster entity) {
        return RequirementMasterResponse.builder()
                .requirementCode(entity.getRequirementCode())
                .requirementName(entity.getRequirementName())
                .category(entity.getCategory())
                .sortOrder(entity.getSortOrder())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
