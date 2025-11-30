package com.shoes.fitness.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_requirement_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessRequirementMaster {

    @Id
    @Column(name = "requirement_code", length = 50)
    private String requirementCode;

    @Column(name = "requirement_name", length = 100, nullable = false)
    private String requirementName;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
