package com.shoes.fitness.domain.minihomepage.dto;

import com.shoes.fitness.entity.FitnessCenterGallery;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryResponse {
    private String galleryId;
    private String centerId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createdAt;

    public static GalleryResponse from(FitnessCenterGallery entity) {
        return GalleryResponse.builder()
                .galleryId(entity.getGalleryId())
                .centerId(entity.getCenterId())
                .imageUrl(entity.getImageUrl())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
