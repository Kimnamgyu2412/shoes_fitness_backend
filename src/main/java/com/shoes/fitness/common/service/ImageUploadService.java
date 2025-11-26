package com.shoes.fitness.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.shoes.fitness.common.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    private final AmazonS3 amazonS3;
    private final FileValidationService fileValidationService;

    @Value("${ncp.object-storage.bucket}")
    private String bucketName;

    @Value("${ncp.object-storage.region}")
    private String region;

    @Value("${ncp.cdn.domain}")
    private String cdnDomain;

    @Value("${ncp.cdn.enabled}")
    private boolean cdnEnabled;

    /**
     * 이미지 파일 업로드
     */
    public ImageUploadResponse uploadImage(MultipartFile file, String type) {
        try {
            // 파일 검증
            FileValidationService.ValidationResult validation = fileValidationService.validateImageFile(file);
            if (!validation.isValid()) {
                throw new IllegalArgumentException("파일 검증 실패: " + validation.getErrorMessage());
            }

            // 업로드 타입 검증
            if (!"product-image".equals(type)) {
                throw new IllegalArgumentException("지원하지 않는 업로드 타입입니다: " + type);
            }

            String fileName = generateFileName(file.getOriginalFilename());
            String key = generateS3Key(getDirectoryByType(type), fileName);

            // S3에 업로드
            ObjectMetadata metadata = createObjectMetadata(file);

            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
                amazonS3.putObject(putObjectRequest);
            }

            // 파일 URL 생성 - NCP CDN 또는 Object Storage 직접 접근
            String imageUrl = generateFileUrl(key);

            log.info("이미지 업로드 완료 - Key: {}, Size: {}", key, file.getSize());

            return ImageUploadResponse.builder()
                    .imageUrl(imageUrl)
                    .fileName(fileName)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .build();

        } catch (IOException e) {
            log.error("이미지 업로드 실패", e);
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }
    }

    /**
     * S3 ObjectMetadata 생성
     */
    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setCacheControl("max-age=2592000"); // 30일 캐시

        // 이미지 최적화를 위한 메타데이터
        metadata.addUserMetadata("uploaded-by", "shoes-admin-backend");
        metadata.addUserMetadata("upload-time", LocalDateTime.now().toString());

        return metadata;
    }

    /**
     * 이미지 파일 삭제
     */
    public void deleteImage(String imageUrl) {
        try {
            // URL에서 S3 key 추출
            String key = extractS3KeyFromUrl(imageUrl);
            if (key != null) {
                amazonS3.deleteObject(bucketName, key);
                log.info("이미지 삭제 완료 - Key: {}", key);
            }
        } catch (Exception e) {
            log.error("이미지 삭제 실패 - URL: {}", imageUrl, e);
            throw new RuntimeException("이미지 삭제에 실패했습니다.", e);
        }
    }

    /**
     * NCP CDN 또는 Object Storage URL 생성
     */
    private String generateFileUrl(String key) {
        if (cdnEnabled && cdnDomain != null && !cdnDomain.isEmpty()) {
            return String.format("https://%s/%s", cdnDomain, key);
        } else {
            return String.format("https://%s.kr.object.ncloudstorage.com/%s", bucketName, key);
        }
    }

    /**
     * URL에서 NCP key 추출
     */
    private String extractS3KeyFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        // CDN URL에서 key 추출
        if (cdnEnabled && cdnDomain != null && !cdnDomain.isEmpty()) {
            String cdnUrlPrefix = String.format("https://%s/", cdnDomain);
            if (imageUrl.startsWith(cdnUrlPrefix)) {
                return imageUrl.substring(cdnUrlPrefix.length());
            }
        }

        // Object Storage 직접 URL에서 key 추출
        String bucketUrlPrefix = String.format("https://%s.kr.object.ncloudstorage.com/", bucketName);
        if (imageUrl.startsWith(bucketUrlPrefix)) {
            return imageUrl.substring(bucketUrlPrefix.length());
        }

        return null;
    }

    /**
     * 업로드 타입별 디렉토리 반환
     */
    private String getDirectoryByType(String type) {
        switch (type) {
            case "product-image":
                return "product-images";
            default:
                return "images";
        }
    }

    /**
     * 고유한 파일명 생성
     */
    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * S3 키 생성 (날짜별 폴더 구조 포함)
     */
    private String generateS3Key(String directory, String fileName) {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%s/%s", directory, datePrefix, fileName);
    }
}
