package com.shoes.fitness.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.shoes.fitness.common.dto.FileUploadResult;
import com.shoes.fitness.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final AmazonS3 amazonS3;
    private final FileUploadConfig fileUploadConfig;
    private final FileValidationService fileValidationService;

    @Value("${ncp.object-storage.bucket}")
    private String bucketName;

    @Value("${ncp.object-storage.region}")
    private String region;

    @Value("${ncp.cdn.domain}")
    private String cdnDomain;

    @Value("${ncp.cdn.enabled}")
    private boolean cdnEnabled;

    // 멀티파트 업로드 임계값 (50MB) - NCP Object Storage 권장값
    private static final long MULTIPART_THRESHOLD = 50 * 1024 * 1024;
    // 멀티파트 청크 크기 (5MB) - 최소 5MB 권장
    private static final long MULTIPART_CHUNK_SIZE = 5 * 1024 * 1024;

    /**
     * 이미지 파일 업로드
     */
    public FileUploadResult uploadImageFile(MultipartFile file, String directory) {
        try {
            // 파일 검증
            FileValidationService.ValidationResult validation = fileValidationService.validateImageFile(file);
            if (!validation.isValid()) {
                throw new IllegalArgumentException("파일 검증 실패: " + validation.getErrorMessage());
            }

            String fileName = generateFileName(file.getOriginalFilename());
            String key = generateS3Key(directory, fileName);

            // 먼저 임시 파일로 저장 (한 번만 읽기)
            File tempFile = saveToTempDirectory(file);

            try {
                // 임시 파일로부터 S3에 업로드
                ObjectMetadata metadata = createObjectMetadata(file);

                // FileInputStream을 사용해서 임시 파일을 읽어서 S3에 업로드
                try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
                    PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, fileInputStream, metadata);
                    amazonS3.putObject(putObjectRequest);
                }

                // 파일 URL 생성 - NCP CDN 또는 Object Storage 직접 접근
                String fileUrl = generateFileUrl(key);

                log.info("이미지 파일 업로드 완료 - Key: {}, Size: {}", key, file.getSize());

                return FileUploadResult.builder()
                        .fileName(fileName)
                        .bucket(bucketName)
                        .key(key)
                        .fileUrl(fileUrl)
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .tempFilePath(tempFile.getAbsolutePath())
                        .build();

            } catch (Exception e) {
                // 에러 발생 시 임시 파일 즉시 삭제
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                throw e;
            }

        } catch (IOException e) {
            log.error("이미지 파일 업로드 실패", e);
            throw new RuntimeException("이미지 파일 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 비디오 파일 업로드 (항상 멀티파트 업로드 사용하여 안정성 확보)
     */
    public FileUploadResult uploadVideoFile(MultipartFile file, String directory) {
        try {
            // 파일 검증
            FileValidationService.ValidationResult validation = fileValidationService.validateVideoFile(file);
            if (!validation.isValid()) {
                throw new IllegalArgumentException("파일 검증 실패: " + validation.getErrorMessage());
            }

            String fileName = generateFileName(file.getOriginalFilename());
            String key = generateS3Key(directory, fileName);

            // 먼저 임시 파일로 저장 (한 번만 읽기)
            File tempFile = saveToTempDirectory(file);

            try {
                // 비디오 파일은 항상 멀티파트 업로드 사용 (네트워크 안정성 확보)
                log.info("비디오 파일 업로드 시작 ({}MB), 멀티파트 업로드 사용 - Key: {}",
                        file.getSize() / 1024 / 1024, key);
                uploadLargeFileWithMultipart(tempFile, key, file.getContentType());

                // 파일 URL 생성 - NCP CDN 또는 Object Storage 직접 접근
                String fileUrl = generateFileUrl(key);

                log.info("파일 업로드 완료 - Key: {}, Size: {}MB", key, file.getSize() / 1024 / 1024);

                return FileUploadResult.builder()
                        .fileName(fileName)
                        .bucket(bucketName)
                        .key(key)
                        .fileUrl(fileUrl)
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .tempFilePath(tempFile.getAbsolutePath())
                        .build();

            } catch (Exception e) {
                // 에러 발생 시 임시 파일 즉시 삭제
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                throw e;
            }

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 대용량 파일을 멀티파트 업로드로 업로드
     */
    private void uploadLargeFileWithMultipart(File file, String key, String contentType) throws IOException {
        // TransferManager 사용 (자동으로 멀티파트 업로드 처리)
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .withMinimumUploadPartSize(MULTIPART_CHUNK_SIZE)
                .withMultipartUploadThreshold(MULTIPART_THRESHOLD)
                .build();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.length());
            metadata.setCacheControl("max-age=86400");

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file)
                    .withMetadata(metadata);

            Upload upload = transferManager.upload(putObjectRequest);

            log.info("멀티파트 업로드 시작 - Key: {}", key);
            upload.waitForCompletion();
            log.info("멀티파트 업로드 완료 - Key: {}", key);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("파일 업로드가 중단되었습니다.", e);
        } finally {
            // shutdownNow() 대신 shutdown(false)를 사용하여 S3Client는 유지
            // false = S3Client를 종료하지 않음 (다른 요청에서 재사용 가능)
            transferManager.shutdownNow(false);
        }
    }

    /**
     * 임시 디렉토리에 파일 저장
     */
    private File saveToTempDirectory(MultipartFile file) throws IOException {
        String tempDir = fileUploadConfig.getTempDir();
        String fileName = generateFileName(file.getOriginalFilename());
        File tempFile = new File(tempDir, fileName);

        // 디렉토리 생성
        tempFile.getParentFile().mkdirs();

        // 파일 저장
        file.transferTo(tempFile);

        return tempFile;
    }

    /**
     * S3 ObjectMetadata 생성
     */
    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setCacheControl("max-age=86400"); // 1일 캐시
        return metadata;
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String bucket, String key) {
        try {
            amazonS3.deleteObject(bucket, key);
            log.info("파일 삭제 완료 - Key: {}", key);
        } catch (Exception e) {
            log.error("파일 삭제 실패 - Key: {}", key, e);
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    /**
     * Pre-signed URL 생성
     */
    public String generatePresignedUrl(String bucket, String key, int expirationInSeconds) {
        try {
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000L * expirationInSeconds;
            expiration.setTime(expTimeMillis);

            return amazonS3.generatePresignedUrl(bucket, key, expiration).toString();
        } catch (Exception e) {
            log.error("Pre-signed URL 생성 실패 - Key: {}", key, e);
            throw new RuntimeException("다운로드 URL 생성에 실패했습니다.", e);
        }
    }

    /**
     * 임시 파일 정리
     */
    public void cleanupTempFile(String tempFilePath) {
        try {
            File tempFile = new File(tempFilePath);
            if (tempFile.exists() && tempFile.delete()) {
                log.debug("임시 파일 삭제 완료: {}", tempFilePath);
            }
        } catch (Exception e) {
            log.warn("임시 파일 삭제 실패: {}", tempFilePath, e);
        }
    }

    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String generateS3Key(String directory, String fileName) {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%s/%s", directory, datePrefix, fileName);
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
}
