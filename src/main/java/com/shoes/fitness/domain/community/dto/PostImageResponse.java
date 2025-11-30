package com.shoes.fitness.domain.community.dto;

import com.shoes.fitness.entity.CommunityPostImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImageResponse {
    private String imageId;
    private String imageUrl;
    private Integer displayOrder;

    public static PostImageResponse from(CommunityPostImage entity) {
        return PostImageResponse.builder()
                .imageId(entity.getImageId())
                .imageUrl(entity.getImageUrl())
                .displayOrder(entity.getSortOrder())
                .build();
    }
}
