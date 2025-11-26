package com.shoes.fitness.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String partnerId;
    private String partnerName;
    private String email;
    private String role;
    private long expiresIn; // 액세스 토큰 만료 시간 (초)
}
