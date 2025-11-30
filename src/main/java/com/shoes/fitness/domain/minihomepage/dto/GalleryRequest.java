package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryRequest {
    private String imageUrl;
    private Integer sortOrder;
}
