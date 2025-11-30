package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryOrderRequest {
    private List<GalleryOrderItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GalleryOrderItem {
        private String galleryId;
        private Integer sortOrder;
    }
}
