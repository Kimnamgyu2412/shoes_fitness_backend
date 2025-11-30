package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_job_posting", indexes = {
        @Index(name = "idx_posting_center_id", columnList = "center_id"),
        @Index(name = "idx_posting_status", columnList = "status"),
        @Index(name = "idx_posting_end_date", columnList = "end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessJobPosting {

    @Id
    @Column(name = "posting_id", length = 32)
    private String postingId;

    @PrePersist
    public void prePersist() {
        if (this.postingId == null) {
            this.postingId = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "center_id", length = 32, nullable = false)
    private String centerId;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 20, nullable = false)
    private EmploymentType employmentType;

    @Column(name = "salary", columnDefinition = "TEXT")
    private String salary;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private PostingStatus status = PostingStatus.ACTIVE;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "apply_count")
    @Builder.Default
    private Integer applyCount = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EmploymentType {
        @JsonEnumValue("full-time")
        FULL_TIME("full-time"),
        @JsonEnumValue("part-time")
        PART_TIME("part-time"),
        @JsonEnumValue("contract")
        CONTRACT("contract"),
        @JsonEnumValue("freelance")
        FREELANCE("freelance");

        private final String value;

        EmploymentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PostingStatus {
        @JsonEnumValue("active")
        ACTIVE("active"),
        @JsonEnumValue("expired")
        EXPIRED("expired"),
        @JsonEnumValue("closed")
        CLOSED("closed");

        private final String value;

        PostingStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // JSON 직렬화를 위한 커스텀 어노테이션 (실제로는 Jackson 설정 필요)
    @interface JsonEnumValue {
        String value();
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
}
