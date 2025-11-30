package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceBatchRequest {
    private List<PriceRequest> prices;
}
