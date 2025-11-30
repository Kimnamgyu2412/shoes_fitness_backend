package com.shoes.fitness.domain.community.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeStatusResponse {
    private String postId;
    private String tradeStatus;
    private LocalDateTime updatedAt;
}
