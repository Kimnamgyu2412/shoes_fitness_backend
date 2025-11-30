package com.shoes.fitness.domain.jobposting.service;

import com.shoes.fitness.domain.jobposting.dto.*;
import com.shoes.fitness.domain.jobposting.repository.JobPostingRepository;
import com.shoes.fitness.domain.jobposting.repository.JobPostingRequirementRepository;
import com.shoes.fitness.domain.jobposting.repository.RequirementMasterRepository;
import com.shoes.fitness.domain.minihomepage.repository.FitnessCenterRepository;
import com.shoes.fitness.entity.FitnessCenter;
import com.shoes.fitness.entity.FitnessJobPosting;
import com.shoes.fitness.entity.FitnessJobPostingRequirement;
import com.shoes.fitness.entity.FitnessRequirementMaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPostingRequirementRepository requirementRepository;
    private final RequirementMasterRepository requirementMasterRepository;
    private final FitnessCenterRepository centerRepository;

    // ==================== 자격요건 마스터 ====================

    public List<RequirementMasterResponse> getRequirementMasterList() {
        return requirementMasterRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(RequirementMasterResponse::from)
                .collect(Collectors.toList());
    }

    // ==================== 채용공고 목록 조회 ====================

    public JobPostingListResponse getPostings(String fitnessId, String status, String keyword, int page, int size) {
        FitnessCenter center = getCenterByFitnessId(fitnessId);

        FitnessJobPosting.PostingStatus postingStatus = null;
        if (status != null && !status.isEmpty()) {
            postingStatus = parseStatus(status);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<FitnessJobPosting> postingsPage = jobPostingRepository.findByFilters(
                center.getCenterId(), postingStatus, keyword, pageable);

        long totalCount = jobPostingRepository.countByFilters(center.getCenterId(), postingStatus, keyword);

        List<JobPostingResponse> postingResponses = postingsPage.getContent().stream()
                .map(posting -> {
                    List<PostingRequirementResponse> requirements = getRequirementsForPosting(posting.getPostingId());
                    return JobPostingResponse.from(posting, requirements);
                })
                .collect(Collectors.toList());

        return JobPostingListResponse.builder()
                .postings(postingResponses)
                .totalCount(totalCount)
                .build();
    }

    // ==================== 채용공고 상세 조회 ====================

    public JobPostingResponse getPostingDetail(String postingId) {
        FitnessJobPosting posting = jobPostingRepository.findById(postingId)
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없습니다. ID: " + postingId));

        List<PostingRequirementResponse> requirements = getRequirementsForPosting(postingId);
        return JobPostingResponse.from(posting, requirements);
    }

    // ==================== 채용공고 등록 ====================

    @Transactional
    public JobPostingResponse createPosting(String fitnessId, JobPostingRequest request) {
        FitnessCenter center = getCenterByFitnessId(fitnessId);

        FitnessJobPosting posting = FitnessJobPosting.builder()
                .centerId(center.getCenterId())
                .title(request.getTitle())
                .employmentType(parseEmploymentType(request.getEmploymentType()))
                .salary(request.getSalary())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .status(FitnessJobPosting.PostingStatus.ACTIVE)
                .viewCount(0)
                .applyCount(0)
                .isActive(true)
                .build();

        FitnessJobPosting saved = jobPostingRepository.save(posting);
        log.info("채용공고 등록. postingId: {}, centerId: {}", saved.getPostingId(), center.getCenterId());

        // 자격요건 저장
        if (request.getRequirementIds() != null && !request.getRequirementIds().isEmpty()) {
            saveRequirements(saved.getPostingId(), request.getRequirementIds());
        }

        List<PostingRequirementResponse> requirements = getRequirementsForPosting(saved.getPostingId());
        return JobPostingResponse.from(saved, requirements);
    }

    // ==================== 채용공고 수정 ====================

    @Transactional
    public JobPostingResponse updatePosting(String fitnessId, String postingId, JobPostingRequest request) {
        FitnessCenter center = getCenterByFitnessId(fitnessId);

        FitnessJobPosting posting = jobPostingRepository.findByPostingIdAndCenterId(postingId, center.getCenterId())
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없거나 권한이 없습니다."));

        posting.setTitle(request.getTitle());
        posting.setEmploymentType(parseEmploymentType(request.getEmploymentType()));
        posting.setSalary(request.getSalary());
        posting.setStartDate(request.getStartDate());
        posting.setEndDate(request.getEndDate());
        posting.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            posting.setStatus(parseStatus(request.getStatus()));
        }

        FitnessJobPosting saved = jobPostingRepository.save(posting);
        log.info("채용공고 수정. postingId: {}", postingId);

        // 자격요건 업데이트
        requirementRepository.deleteByPostingId(postingId);
        if (request.getRequirementIds() != null && !request.getRequirementIds().isEmpty()) {
            saveRequirements(postingId, request.getRequirementIds());
        }

        List<PostingRequirementResponse> requirements = getRequirementsForPosting(saved.getPostingId());
        return JobPostingResponse.from(saved, requirements);
    }

    // ==================== 채용공고 삭제 ====================

    @Transactional
    public void deletePosting(String fitnessId, String postingId) {
        FitnessCenter center = getCenterByFitnessId(fitnessId);

        FitnessJobPosting posting = jobPostingRepository.findByPostingIdAndCenterId(postingId, center.getCenterId())
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없거나 권한이 없습니다."));

        posting.setIsActive(false);
        jobPostingRepository.save(posting);
        log.info("채용공고 삭제 (soft delete). postingId: {}", postingId);
    }

    // ==================== 상태 변경 ====================

    @Transactional
    public StatusChangeResponse changeStatus(String fitnessId, String postingId, StatusChangeRequest request) {
        FitnessCenter center = getCenterByFitnessId(fitnessId);

        FitnessJobPosting posting = jobPostingRepository.findByPostingIdAndCenterId(postingId, center.getCenterId())
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없거나 권한이 없습니다."));

        posting.setStatus(parseStatus(request.getStatus()));
        FitnessJobPosting saved = jobPostingRepository.save(posting);
        log.info("채용공고 상태 변경. postingId: {}, status: {}", postingId, request.getStatus());

        return StatusChangeResponse.builder()
                .postingId(saved.getPostingId())
                .status(saved.getStatus().getValue())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // ==================== 조회수 증가 ====================

    @Transactional
    public void incrementViewCount(String postingId) {
        jobPostingRepository.incrementViewCount(postingId);
        log.debug("조회수 증가. postingId: {}", postingId);
    }

    // ==================== 통계 조회 ====================

    public JobPostingStatsResponse getStats(String fitnessId, String postingId) {
        FitnessCenter center = getCenterByFitnessId(fitnessId);

        FitnessJobPosting posting = jobPostingRepository.findByPostingIdAndCenterId(postingId, center.getCenterId())
                .orElseThrow(() -> new IllegalArgumentException("채용공고를 찾을 수 없거나 권한이 없습니다."));

        return JobPostingStatsResponse.builder()
                .viewCount(posting.getViewCount())
                .applyCount(posting.getApplyCount())
                .build();
    }

    // ==================== Helper Methods ====================

    private FitnessCenter getCenterByFitnessId(String fitnessId) {
        return centerRepository.findByFitnessId(fitnessId)
                .orElseThrow(() -> new IllegalArgumentException("센터를 찾을 수 없습니다. 먼저 센터 정보를 등록해주세요."));
    }

    private void saveRequirements(String postingId, List<String> requirementIds) {
        for (String requirementId : requirementIds) {
            FitnessJobPostingRequirement requirement = FitnessJobPostingRequirement.builder()
                    .postingId(postingId)
                    .requirementId(requirementId)
                    .build();
            requirementRepository.save(requirement);
        }
    }

    private List<PostingRequirementResponse> getRequirementsForPosting(String postingId) {
        List<FitnessJobPostingRequirement> postingRequirements = requirementRepository.findByPostingId(postingId);

        if (postingRequirements.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> codes = postingRequirements.stream()
                .map(FitnessJobPostingRequirement::getRequirementId)
                .collect(Collectors.toList());

        Map<String, FitnessRequirementMaster> masterMap = requirementMasterRepository.findByRequirementCodeIn(codes)
                .stream()
                .collect(Collectors.toMap(FitnessRequirementMaster::getRequirementCode, m -> m));

        return postingRequirements.stream()
                .map(pr -> {
                    FitnessRequirementMaster master = masterMap.get(pr.getRequirementId());
                    if (master != null) {
                        return PostingRequirementResponse.from(master);
                    }
                    return PostingRequirementResponse.builder()
                            .requirementCode(pr.getRequirementId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private FitnessJobPosting.EmploymentType parseEmploymentType(String value) {
        if (value == null) return null;
        switch (value.toLowerCase()) {
            case "full-time": return FitnessJobPosting.EmploymentType.FULL_TIME;
            case "part-time": return FitnessJobPosting.EmploymentType.PART_TIME;
            case "contract": return FitnessJobPosting.EmploymentType.CONTRACT;
            case "freelance": return FitnessJobPosting.EmploymentType.FREELANCE;
            default: throw new IllegalArgumentException("유효하지 않은 고용 형태입니다: " + value);
        }
    }

    private FitnessJobPosting.PostingStatus parseStatus(String value) {
        if (value == null) return null;
        switch (value.toLowerCase()) {
            case "active": return FitnessJobPosting.PostingStatus.ACTIVE;
            case "expired": return FitnessJobPosting.PostingStatus.EXPIRED;
            case "closed": return FitnessJobPosting.PostingStatus.CLOSED;
            default: throw new IllegalArgumentException("유효하지 않은 상태입니다: " + value);
        }
    }
}
