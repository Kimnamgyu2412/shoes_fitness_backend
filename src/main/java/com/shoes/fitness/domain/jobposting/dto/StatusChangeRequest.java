package com.shoes.fitness.domain.jobposting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusChangeRequest {
    private String status;
}
