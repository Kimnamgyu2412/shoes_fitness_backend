package com.shoes.fitness.domain.jobposting.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusChangeResponse {
    private String postingId;
    private String status;
    private LocalDateTime updatedAt;
}
