package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {
    private String name;
    private String description;
    private String imageUrl;
    private String discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean isActive;
}
