package com.shoes.fitness.domain.minihomepage.dto;

import com.shoes.fitness.entity.FitnessFacilityMaster;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityMasterResponse {
    private String facilityCode;
    private String facilityName;
    private String iconUrl;
    private Integer sortOrder;
    private Boolean isActive;

    public static FacilityMasterResponse from(FitnessFacilityMaster entity) {
        return FacilityMasterResponse.builder()
                .facilityCode(entity.getFacilityCode())
                .facilityName(entity.getFacilityName())
                .iconUrl(entity.getIconUrl())
                .sortOrder(entity.getSortOrder())
                .isActive(entity.getIsActive())
                .build();
    }
}
