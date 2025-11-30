package com.shoes.fitness.domain.jobposting.dto;

import com.shoes.fitness.entity.FitnessJobPosting;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingResponse {
    private String postingId;
    private String centerId;
    private String title;
    private String employmentType;
    private String salary;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String status;
    private Integer viewCount;
    private Integer applyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PostingRequirementResponse> requirements;

    public static JobPostingResponse from(FitnessJobPosting entity) {
        return JobPostingResponse.builder()
                .postingId(entity.getPostingId())
                .centerId(entity.getCenterId())
                .title(entity.getTitle())
                .employmentType(entity.getEmploymentType() != null ? entity.getEmploymentType().getValue() : null)
                .salary(entity.getSalary())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .description(entity.getDescription())
                .status(entity.getStatus() != null ? entity.getStatus().getValue() : null)
                .viewCount(entity.getViewCount())
                .applyCount(entity.getApplyCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static JobPostingResponse from(FitnessJobPosting entity, List<PostingRequirementResponse> requirements) {
        JobPostingResponse response = from(entity);
        response.setRequirements(requirements);
        return response;
    }
}
