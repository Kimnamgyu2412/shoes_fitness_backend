package com.shoes.fitness.common.service;

import com.shoes.fitness.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileValidationService {

    private final FileUploadConfig fileUploadConfig;

    /**
     * 이미지 파일 유효성 검사
     */
    public ValidationResult validateImageFile(MultipartFile file) {
        List<String> errors = new ArrayList<>();

        // 파일 존재 확인
        if (file == null || file.isEmpty()) {
            errors.add("파일이 선택되지 않았습니다.");
            return new ValidationResult(false, errors);
        }

        // 파일 크기 확인 (이미지는 5MB 제한)
        long maxImageSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxImageSize) {
            errors.add("이미지 파일 크기가 5MB를 초과합니다.");
        }

        // 파일 타입 확인
        if (!isAllowedImageType(file.getContentType())) {
            errors.add("지원하지 않는 이미지 형식입니다. (JPEG, PNG, WebP, GIF만 지원)");
        }

        // 파일명 확인
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            errors.add("파일명이 올바르지 않습니다.");
        } else if (originalFilename.length() > 255) {
            errors.add("파일명이 너무 깁니다. (최대 255자)");
        }

        // 확장자 확인
        if (originalFilename != null && !hasValidImageExtension(originalFilename)) {
            errors.add("지원하지 않는 이미지 확장자입니다.");
        }

        boolean isValid = errors.isEmpty();
        log.debug("이미지 파일 검증 결과 - 파일명: {}, 유효성: {}, 오류 수: {}",
                originalFilename, isValid, errors.size());

        return new ValidationResult(isValid, errors);
    }

    /**
     * 파일 유효성 검사
     */
    public ValidationResult validateVideoFile(MultipartFile file) {
        List<String> errors = new ArrayList<>();

        // 파일 존재 확인
        if (file == null || file.isEmpty()) {
            errors.add("파일이 선택되지 않았습니다.");
            return new ValidationResult(false, errors);
        }

        // 파일 크기 확인
        if (!fileUploadConfig.isValidFileSize(file.getSize())) {
            errors.add(String.format("파일 크기가 %.1fMB를 초과합니다.", fileUploadConfig.getMaxFileSizeMB()));
        }

        // 파일 타입 확인
        if (!fileUploadConfig.isAllowedVideoType(file.getContentType())) {
            errors.add("지원하지 않는 파일 형식입니다. (MP4, WebM, MOV, AVI만 지원)");
        }

        // 파일명 확인
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            errors.add("파일명이 올바르지 않습니다.");
        } else if (originalFilename.length() > 255) {
            errors.add("파일명이 너무 깁니다. (최대 255자)");
        }

        // 확장자 확인
        if (originalFilename != null && !hasValidVideoExtension(originalFilename)) {
            errors.add("지원하지 않는 파일 확장자입니다.");
        }

        boolean isValid = errors.isEmpty();
        log.debug("파일 검증 결과 - 파일명: {}, 유효성: {}, 오류 수: {}",
                originalFilename, isValid, errors.size());

        return new ValidationResult(isValid, errors);
    }

    /**
     * 이미지 Content-Type 확인
     */
    private boolean isAllowedImageType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/webp") ||
                contentType.equals("image/gif");
    }

    /**
     * 이미지 파일 확장자 확인
     */
    private boolean hasValidImageExtension(String filename) {
        String lowercaseFilename = filename.toLowerCase();
        return lowercaseFilename.endsWith(".jpg") ||
                lowercaseFilename.endsWith(".jpeg") ||
                lowercaseFilename.endsWith(".png") ||
                lowercaseFilename.endsWith(".webp") ||
                lowercaseFilename.endsWith(".gif");
    }

    /**
     * 비디오 파일 확장자 확인
     */
    private boolean hasValidVideoExtension(String filename) {
        String lowercaseFilename = filename.toLowerCase();
        return lowercaseFilename.endsWith(".mp4") ||
                lowercaseFilename.endsWith(".webm") ||
                lowercaseFilename.endsWith(".mov") ||
                lowercaseFilename.endsWith(".avi");
    }

    /**
     * 검증 결과 클래스
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
}
