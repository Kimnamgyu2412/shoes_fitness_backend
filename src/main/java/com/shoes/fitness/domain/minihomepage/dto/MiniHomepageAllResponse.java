package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiniHomepageAllResponse {
    private CenterInfoResponse centerInfo;
    private List<GalleryResponse> galleries;
    private OperationHoursResponse operationHours;
    private List<PriceResponse> prices;
    private List<CenterFacilityResponse> facilities;
    private List<EventResponse> events;
}
