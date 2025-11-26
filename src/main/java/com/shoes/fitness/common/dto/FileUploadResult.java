package com.shoes.fitness.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResult {
    private String fileName;
    private String bucket;
    private String key;
    private String fileUrl;
    private Long fileSize;
    private String contentType;
    private String tempFilePath;
}
