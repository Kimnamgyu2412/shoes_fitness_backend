package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CenterInfoRequest {
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
}
