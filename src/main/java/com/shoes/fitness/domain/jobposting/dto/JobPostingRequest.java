package com.shoes.fitness.domain.jobposting.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingRequest {
    private String title;
    private String employmentType;
    private String salary;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private List<String> requirementIds;
    private String status;
}
