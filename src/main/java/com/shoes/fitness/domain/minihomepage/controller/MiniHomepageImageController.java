package com.shoes.fitness.domain.minihomepage.controller;

import com.shoes.fitness.common.dto.ApiResponse;
import com.shoes.fitness.common.dto.ImageUploadResponse;
import com.shoes.fitness.common.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/fitness/mini-homepage/upload")
@RequiredArgsConstructor
public class MiniHomepageImageController {

    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadThumbnail(@RequestParam("file") MultipartFile file) {
        ImageUploadResponse response = imageUploadService.uploadImage(file, "thumbnail");
        return ResponseEntity.ok(ApiResponse.success("대표 이미지가 업로드되었습니다.", response));
    }

    @PostMapping(value = "/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadGalleryImage(@RequestParam("file") MultipartFile file) {
        ImageUploadResponse response = imageUploadService.uploadImage(file, "gallery");
        return ResponseEntity.ok(ApiResponse.success("갤러리 이미지가 업로드되었습니다.", response));
    }

    @PostMapping(value = "/event", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadEventImage(@RequestParam("file") MultipartFile file) {
        ImageUploadResponse response = imageUploadService.uploadImage(file, "event");
        return ResponseEntity.ok(ApiResponse.success("이벤트 이미지가 업로드되었습니다.", response));
    }
}
