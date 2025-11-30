package com.shoes.fitness.domain.community.controller;

import com.shoes.fitness.common.dto.ApiResponse;
import com.shoes.fitness.common.security.CurrentUser;
import com.shoes.fitness.common.security.UserPrincipal;
import com.shoes.fitness.common.service.ImageUploadService;
import com.shoes.fitness.domain.community.dto.*;
import com.shoes.fitness.domain.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fitness/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final ImageUploadService imageUploadService;

    // ==================== 게시글 목록 조회 ====================

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PostListResponse>> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PostListResponse response = communityService.getPosts(category, status, location, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 게시글 상세 조회 ====================

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(
            @PathVariable String postId,
            @CurrentUser UserPrincipal userPrincipal) {
        String userId = userPrincipal != null ? userPrincipal.getFitnessId() : null;
        PostDetailResponse response = communityService.getPostDetail(postId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 게시글 조회수 증가 ====================

    @PostMapping("/posts/{postId}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable String postId) {
        communityService.incrementViewCount(postId);
        return ResponseEntity.ok(ApiResponse.successWithMessage("조회수가 증가되었습니다."));
    }

    // ==================== 게시글 등록 ====================

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody PostRequest request) {
        PostResponse response = communityService.createPost(
                userPrincipal.getFitnessId(),
                userPrincipal.getOwnerName(),
                null, // 프로필 이미지 - 필요시 UserPrincipal에서 가져오기
                null, // centerId - 필요시 센터 정보에서 가져오기
                request
        );
        return ResponseEntity.ok(ApiResponse.success("게시글이 등록되었습니다.", response));
    }

    // ==================== 게시글 수정 ====================

    @PutMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable String postId,
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody PostRequest request) {
        PostResponse response = communityService.updatePost(postId, userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", response));
    }

    // ==================== 게시글 삭제 ====================

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable String postId,
            @CurrentUser UserPrincipal userPrincipal) {
        communityService.deletePost(postId, userPrincipal.getFitnessId());
        return ResponseEntity.ok(ApiResponse.successWithMessage("게시글이 삭제되었습니다."));
    }

    // ==================== 거래 상태 변경 ====================

    @PatchMapping("/posts/{postId}/trade-status")
    public ResponseEntity<ApiResponse<TradeStatusResponse>> changeTradeStatus(
            @PathVariable String postId,
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody TradeStatusRequest request) {
        TradeStatusResponse response = communityService.changeTradeStatus(
                postId, userPrincipal.getFitnessId(), request.getTradeStatus());
        return ResponseEntity.ok(ApiResponse.success("거래 상태가 변경되었습니다.", response));
    }

    // ==================== 좋아요 토글 ====================

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(
            @PathVariable String postId,
            @CurrentUser UserPrincipal userPrincipal) {
        LikeResponse response = communityService.toggleLike(postId, userPrincipal.getFitnessId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 댓글 목록 조회 ====================

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable String postId) {
        List<CommentResponse> response = communityService.getComments(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 댓글 등록 ====================

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody CommentRequest request) {
        CommentResponse response = communityService.createComment(
                userPrincipal.getFitnessId(),
                userPrincipal.getOwnerName(),
                null, // 프로필 이미지
                request
        );
        return ResponseEntity.ok(ApiResponse.success("댓글이 등록되었습니다.", response));
    }

    // ==================== 댓글 삭제 ====================

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable String commentId,
            @CurrentUser UserPrincipal userPrincipal) {
        communityService.deleteComment(commentId, userPrincipal.getFitnessId());
        return ResponseEntity.ok(ApiResponse.successWithMessage("댓글이 삭제되었습니다."));
    }

    // ==================== 협력사 목록 조회 ====================

    @GetMapping("/partners")
    public ResponseEntity<ApiResponse<PartnerListResponse>> getPartners(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PartnerListResponse response = communityService.getPartners(category, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 이미지 업로드 ====================

    @PostMapping("/images")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImages(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam("files") List<MultipartFile> files) {

        if (files.size() > 10) {
            throw new IllegalArgumentException("최대 10개의 이미지만 업로드할 수 있습니다.");
        }

        List<ImageUploadResponse.ImageInfo> imageInfos = new ArrayList<>();
        for (MultipartFile file : files) {
            com.shoes.fitness.common.dto.ImageUploadResponse uploadResult = imageUploadService.uploadImage(file, "community");
            imageInfos.add(ImageUploadResponse.ImageInfo.builder()
                    .originalName(file.getOriginalFilename())
                    .url(uploadResult.getImageUrl())
                    .build());
        }

        ImageUploadResponse response = ImageUploadResponse.builder()
                .images(imageInfos)
                .build();

        return ResponseEntity.ok(ApiResponse.success("이미지가 업로드되었습니다.", response));
    }
}
