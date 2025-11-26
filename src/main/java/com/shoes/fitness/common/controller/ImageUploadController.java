package com.shoes.fitness.common.controller;

import com.shoes.fitness.common.dto.ApiResponse;
import com.shoes.fitness.common.dto.ImageUploadResponse;
import com.shoes.fitness.common.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/fitness/upload")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    /**
     * 이미지 파일 업로드
     */
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam(value = "originalFileName", required = false) String originalFileName) {

        try {
            // originalFileName이 제공되지 않으면 업로드된 파일의 원본명 사용
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                originalFileName = file.getOriginalFilename();
            }

            log.info("이미지 업로드 요청 - 타입: {}, 원본 파일명: {}, 업로드 파일명: {}, 크기: {}",
                    type, originalFileName, file.getOriginalFilename(), file.getSize());

            ImageUploadResponse response = imageUploadService.uploadImage(file, type);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("이미지가 성공적으로 업로드되었습니다", response));

        } catch (IllegalArgumentException e) {
            log.error("이미지 업로드 검증 실패 - 타입: {}, 파일명: {}, 오류: {}",
                    type, originalFileName, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), ImageUploadResponse.class));

        } catch (Exception e) {
            log.error("이미지 업로드 실패 - 타입: {}, 파일명: {}", type, originalFileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("이미지 업로드 중 오류가 발생했습니다", ImageUploadResponse.class));
        }
    }

    /**
     * 이미지 파일 삭제 (선택적 기능)
     */
    @DeleteMapping("/image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            log.info("이미지 삭제 요청 - URL: {}", imageUrl);

            imageUploadService.deleteImage(imageUrl);

            return ResponseEntity.ok(ApiResponse.success("이미지가 성공적으로 삭제되었습니다", (Void) null));

        } catch (Exception e) {
            log.error("이미지 삭제 실패 - URL: {}", imageUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("이미지 삭제 중 오류가 발생했습니다"));
        }
    }
}
