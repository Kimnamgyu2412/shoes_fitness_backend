package com.shoes.fitness.domain.minihomepage.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationHoursRequest {
    private Boolean isAlwaysOpen;
    private String alwaysOpenStart;
    private String alwaysOpenEnd;
    private Boolean weekdayClosed;
    private String weekdayOpen;
    private String weekdayClose;
    private Boolean saturdayClosed;
    private String saturdayOpen;
    private String saturdayClose;
    private Boolean sundayClosed;
    private String sundayOpen;
    private String sundayClose;
    private Boolean holidayClosed;
    private String holidayOpen;
    private String holidayClose;

    public LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        // "24:00"을 "23:59"로 변환 (자정 표현)
        if ("24:00".equals(timeStr)) {
            return LocalTime.of(23, 59);
        }
        return LocalTime.parse(timeStr);
    }

    public LocalTime getAlwaysOpenStartAsTime() { return parseTime(alwaysOpenStart); }
    public LocalTime getAlwaysOpenEndAsTime() { return parseTime(alwaysOpenEnd); }
    public LocalTime getWeekdayOpenAsTime() { return parseTime(weekdayOpen); }
    public LocalTime getWeekdayCloseAsTime() { return parseTime(weekdayClose); }
    public LocalTime getSaturdayOpenAsTime() { return parseTime(saturdayOpen); }
    public LocalTime getSaturdayCloseAsTime() { return parseTime(saturdayClose); }
    public LocalTime getSundayOpenAsTime() { return parseTime(sundayOpen); }
    public LocalTime getSundayCloseAsTime() { return parseTime(sundayClose); }
    public LocalTime getHolidayOpenAsTime() { return parseTime(holidayOpen); }
    public LocalTime getHolidayCloseAsTime() { return parseTime(holidayClose); }
}
