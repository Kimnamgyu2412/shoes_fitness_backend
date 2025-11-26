package com.shoes.fitness.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessLoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private FitnessPartnerInfo fitnessPartner;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FitnessPartnerInfo {
        private String fitnessId;
        private String fitnessLoginId;
        private String ownerName;
        private String ownerEmail;
        private String ownerPhone;
        private String gymName;
        private String gymType;
        private String franchiseName;
        private String partnerStatus;
    }
}
