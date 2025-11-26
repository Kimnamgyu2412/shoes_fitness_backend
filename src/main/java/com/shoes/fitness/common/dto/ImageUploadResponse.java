package com.shoes.fitness.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageUploadResponse {
    private String imageUrl;
    private String fileName;
    private Long fileSize;
    private String contentType;
}