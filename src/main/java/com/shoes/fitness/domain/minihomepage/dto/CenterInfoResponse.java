package com.shoes.fitness.domain.minihomepage.dto;

import com.shoes.fitness.entity.FitnessCenter;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CenterInfoResponse {
    private String centerId;
    private String fitnessId;
    private String centerName;
    private String category;
    private String phone;
    private String email;
    private String description;
    private String address;
    private String addressDetail;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isPublic;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CenterInfoResponse from(FitnessCenter entity) {
        return CenterInfoResponse.builder()
                .centerId(entity.getCenterId())
                .fitnessId(entity.getFitnessId())
                .centerName(entity.getCenterName())
                .category(entity.getCategory())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .description(entity.getDescription())
                .address(entity.getAddress())
                .addressDetail(entity.getAddressDetail())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .isPublic(entity.getIsPublic())
                .thumbnailUrl(entity.getThumbnailUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
