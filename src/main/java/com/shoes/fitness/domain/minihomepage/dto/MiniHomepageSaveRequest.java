package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiniHomepageSaveRequest {
    private CenterInfoRequest center;
    private List<GalleryRequest> gallery;
    private OperationHoursRequest operationHours;
    private List<PriceRequest> prices;
    private List<String> facilities;
    private List<EventRequest> events;
}
