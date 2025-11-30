package com.shoes.fitness.domain.minihomepage.dto;

import com.shoes.fitness.entity.FitnessCenterOperationHours;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationHoursResponse {
    private String operationId;
    private String centerId;
    private Boolean isAlwaysOpen;
    private LocalTime alwaysOpenStart;
    private LocalTime alwaysOpenEnd;
    private Boolean weekdayClosed;
    private LocalTime weekdayOpen;
    private LocalTime weekdayClose;
    private Boolean saturdayClosed;
    private LocalTime saturdayOpen;
    private LocalTime saturdayClose;
    private Boolean sundayClosed;
    private LocalTime sundayOpen;
    private LocalTime sundayClose;
    private Boolean holidayClosed;
    private LocalTime holidayOpen;
    private LocalTime holidayClose;

    public static OperationHoursResponse from(FitnessCenterOperationHours entity) {
        return OperationHoursResponse.builder()
                .operationId(entity.getOperationId())
                .centerId(entity.getCenterId())
                .isAlwaysOpen(entity.getIsAlwaysOpen())
                .alwaysOpenStart(entity.getAlwaysOpenStart())
                .alwaysOpenEnd(entity.getAlwaysOpenEnd())
                .weekdayClosed(entity.getWeekdayClosed())
                .weekdayOpen(entity.getWeekdayOpen())
                .weekdayClose(entity.getWeekdayClose())
                .saturdayClosed(entity.getSaturdayClosed())
                .saturdayOpen(entity.getSaturdayOpen())
                .saturdayClose(entity.getSaturdayClose())
                .sundayClosed(entity.getSundayClosed())
                .sundayOpen(entity.getSundayOpen())
                .sundayClose(entity.getSundayClose())
                .holidayClosed(entity.getHolidayClosed())
                .holidayOpen(entity.getHolidayOpen())
                .holidayClose(entity.getHolidayClose())
                .build();
    }
}
