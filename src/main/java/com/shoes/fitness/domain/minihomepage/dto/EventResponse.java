package com.shoes.fitness.domain.minihomepage.dto;

import com.shoes.fitness.entity.FitnessCenterEvent;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private String eventId;
    private String centerId;
    private String name;
    private String description;
    private String imageUrl;
    private String discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventResponse from(FitnessCenterEvent entity) {
        return EventResponse.builder()
                .eventId(entity.getEventId())
                .centerId(entity.getCenterId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .discountRate(entity.getDiscountRate())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
