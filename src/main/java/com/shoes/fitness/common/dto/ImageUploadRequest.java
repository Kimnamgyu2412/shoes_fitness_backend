package com.shoes.fitness.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageUploadRequest {
    
    @NotBlank(message = "업로드 타입은 필수입니다")
    private String type;
    
    private String originalFileName;
}