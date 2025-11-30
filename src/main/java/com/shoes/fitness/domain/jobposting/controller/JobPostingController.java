package com.shoes.fitness.domain.jobposting.controller;

import com.shoes.fitness.common.dto.ApiResponse;
import com.shoes.fitness.common.security.CurrentUser;
import com.shoes.fitness.common.security.UserPrincipal;
import com.shoes.fitness.domain.jobposting.dto.*;
import com.shoes.fitness.domain.jobposting.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fitness/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // ==================== 자격요건 마스터 목록 조회 ====================

    @GetMapping("/requirements/master")
    public ResponseEntity<ApiResponse<List<RequirementMasterResponse>>> getRequirementMasterList() {
        List<RequirementMasterResponse> response = jobPostingService.getRequirementMasterList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 채용공고 목록 조회 ====================

    @GetMapping
    public ResponseEntity<ApiResponse<JobPostingListResponse>> getPostings(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        JobPostingListResponse response = jobPostingService.getPostings(
                userPrincipal.getFitnessId(), status, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 채용공고 상세 조회 ====================

    @GetMapping("/{postingId}")
    public ResponseEntity<ApiResponse<JobPostingResponse>> getPostingDetail(@PathVariable String postingId) {
        JobPostingResponse response = jobPostingService.getPostingDetail(postingId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== 채용공고 등록 ====================

    @PostMapping
    public ResponseEntity<ApiResponse<JobPostingResponse>> createPosting(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody JobPostingRequest request) {
        JobPostingResponse response = jobPostingService.createPosting(userPrincipal.getFitnessId(), request);
        return ResponseEntity.ok(ApiResponse.success("채용공고가 등록되었습니다.", response));
    }

    // ==================== 채용공고 수정 ====================

    @PutMapping("/{postingId}")
    public ResponseEntity<ApiResponse<JobPostingResponse>> updatePosting(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String postingId,
            @RequestBody JobPostingRequest request) {
        JobPostingResponse response = jobPostingService.updatePosting(
                userPrincipal.getFitnessId(), postingId, request);
        return ResponseEntity.ok(ApiResponse.success("채용공고가 수정되었습니다.", response));
    }

    // ==================== 채용공고 삭제 ====================

    @DeleteMapping("/{postingId}")
    public ResponseEntity<ApiResponse<Void>> deletePosting(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String postingId) {
        jobPostingService.deletePosting(userPrincipal.getFitnessId(), postingId);
        return ResponseEntity.ok(ApiResponse.successWithMessage("채용공고가 삭제되었습니다."));
    }

    // ==================== 채용공고 상태 변경 ====================

    @PatchMapping("/{postingId}/status")
    public ResponseEntity<ApiResponse<StatusChangeResponse>> changeStatus(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String postingId,
            @RequestBody StatusChangeRequest request) {
        StatusChangeResponse response = jobPostingService.changeStatus(
                userPrincipal.getFitnessId(), postingId, request);
        return ResponseEntity.ok(ApiResponse.success("공고 상태가 변경되었습니다.", response));
    }

    // ==================== 조회수 증가 ====================

    @PostMapping("/{postingId}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable String postingId) {
        jobPostingService.incrementViewCount(postingId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // ==================== 통계 조회 ====================

    @GetMapping("/{postingId}/stats")
    public ResponseEntity<ApiResponse<JobPostingStatsResponse>> getStats(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String postingId) {
        JobPostingStatsResponse response = jobPostingService.getStats(
                userPrincipal.getFitnessId(), postingId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
