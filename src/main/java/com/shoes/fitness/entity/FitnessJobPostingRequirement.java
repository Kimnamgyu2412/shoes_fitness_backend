package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_job_posting_requirement", indexes = {
        @Index(name = "idx_requirement_posting_id", columnList = "posting_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessJobPostingRequirement {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UuidUtil.generateShortUuid();
        }
    }

    @Column(name = "posting_id", length = 32, nullable = false)
    private String postingId;

    @Column(name = "requirement_code", length = 50, nullable = false)
    private String requirementId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
