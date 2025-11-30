package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "fitness_center_operation_hours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessCenterOperationHours {

    @Id
    @Column(name = "operation_id", length = 32)
    private String operationId;

    @PrePersist
    public void prePersist() {
        if (this.operationId == null) {
            this.operationId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "center_id", length = 32, nullable = false, unique = true)
    private String centerId;

    @Column(name = "is_always_open")
    @Builder.Default
    private Boolean isAlwaysOpen = false;

    @Column(name = "always_open_start")
    private LocalTime alwaysOpenStart;

    @Column(name = "always_open_end")
    private LocalTime alwaysOpenEnd;

    @Column(name = "weekday_closed")
    @Builder.Default
    private Boolean weekdayClosed = false;

    @Column(name = "weekday_open")
    private LocalTime weekdayOpen;

    @Column(name = "weekday_close")
    private LocalTime weekdayClose;

    @Column(name = "saturday_closed")
    @Builder.Default
    private Boolean saturdayClosed = false;

    @Column(name = "saturday_open")
    private LocalTime saturdayOpen;

    @Column(name = "saturday_close")
    private LocalTime saturdayClose;

    @Column(name = "sunday_closed")
    @Builder.Default
    private Boolean sundayClosed = false;

    @Column(name = "sunday_open")
    private LocalTime sundayOpen;

    @Column(name = "sunday_close")
    private LocalTime sundayClose;

    @Column(name = "holiday_closed")
    @Builder.Default
    private Boolean holidayClosed = true;

    @Column(name = "holiday_open")
    private LocalTime holidayOpen;

    @Column(name = "holiday_close")
    private LocalTime holidayClose;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
