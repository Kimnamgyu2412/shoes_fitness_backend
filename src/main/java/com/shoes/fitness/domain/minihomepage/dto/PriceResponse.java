package com.shoes.fitness.domain.minihomepage.dto;

import com.shoes.fitness.entity.FitnessCenterPrice;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceResponse {
    private String priceId;
    private String centerId;
    private String name;
    private String duration;
    private Integer price;
    private Integer discountPrice;
    private Integer sortOrder;
    private Boolean isActive;

    public static PriceResponse from(FitnessCenterPrice entity) {
        return PriceResponse.builder()
                .priceId(entity.getPriceId())
                .centerId(entity.getCenterId())
                .name(entity.getName())
                .duration(entity.getDuration())
                .price(entity.getPrice())
                .discountPrice(entity.getDiscountPrice())
                .sortOrder(entity.getSortOrder())
                .isActive(entity.getIsActive())
                .build();
    }
}
