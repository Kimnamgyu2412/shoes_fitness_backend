package com.shoes.fitness.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "file.upload")
@Data
public class FileUploadConfig {

    /**
     * 임시 파일 저장 디렉토리
     */
    private String tempDir = "/tmp/shoebox-uploads";

    /**
     * 최대 파일 크기 (bytes)
     */
    private long maxFileSize = 104857600L; // 100MB

    /**
     * 허용되는 비디오 파일 타입들
     */
    private List<String> allowedVideoTypes = List.of(
            "video/mp4",
            "video/webm",
            "video/quicktime",
            "video/x-msvideo"
    );

    /**
     * 파일 타입이 허용되는지 확인
     */
    public boolean isAllowedVideoType(String contentType) {
        return contentType != null && allowedVideoTypes.contains(contentType.toLowerCase());
    }

    /**
     * 파일 크기가 허용 범위인지 확인
     */
    public boolean isValidFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= maxFileSize;
    }

    /**
     * MB 단위로 최대 파일 크기 반환
     */
    public double getMaxFileSizeMB() {
        return maxFileSize / (1024.0 * 1024.0);
    }
}
