package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityBatchRequest {
    private List<String> facilityCodes;
}
