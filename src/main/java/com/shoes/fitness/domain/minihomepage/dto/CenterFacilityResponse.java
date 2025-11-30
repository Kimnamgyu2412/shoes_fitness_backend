package com.shoes.fitness.domain.minihomepage.dto;

import com.shoes.fitness.entity.FitnessCenterFacility;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CenterFacilityResponse {
    private String id;
    private String centerId;
    private String facilityCode;
    private Boolean isActive;

    public static CenterFacilityResponse from(FitnessCenterFacility entity) {
        return CenterFacilityResponse.builder()
                .id(entity.getId())
                .centerId(entity.getCenterId())
                .facilityCode(entity.getFacilityCode())
                .isActive(entity.getIsActive())
                .build();
    }
}
