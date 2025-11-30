package com.shoes.fitness.domain.community.dto;

import com.shoes.fitness.entity.CommunityPartner;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerResponse {
    private String partnerId;
    private String name;
    private String category;
    private String logoUrl;
    private String description;
    private String discountInfo;
    private String contact;
    private String address;
    private String website;

    public static PartnerResponse from(CommunityPartner entity) {
        return PartnerResponse.builder()
                .partnerId(entity.getPartnerId())
                .name(entity.getName())
                .category(entity.getCategory())
                .logoUrl(entity.getLogoUrl())
                .description(entity.getDescription())
                .discountInfo(entity.getDiscountInfo())
                .contact(entity.getContact())
                .address(entity.getAddress())
                .website(entity.getWebsite())
                .build();
    }
}
