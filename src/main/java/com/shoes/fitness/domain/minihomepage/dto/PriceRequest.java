package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceRequest {
    private String name;
    private String duration;
    private Integer price;
    private Integer discountPrice;
    private Integer sortOrder;
    private Boolean isActive;
}
