package com.shoes.fitness.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessPartner {

    @Id
    @Column(name = "fitness_id", length = 32)
    private String fitnessId;

    @Column(name = "fitness_login_id", length = 50, nullable = false, unique = true)
    private String fitnessLoginId;

    @Column(name = "fitness_password", length = 255, nullable = false)
    private String fitnessPassword;

    // 운영자 기본 정보
    @Column(name = "owner_name", length = 100, nullable = false)
    private String ownerName;

    @Column(name = "owner_phone", length = 20, nullable = false)
    private String ownerPhone;

    @Column(name = "owner_email", length = 100, nullable = false, unique = true)
    private String ownerEmail;

    @Column(name = "owner_birth_date")
    private LocalDate ownerBirthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_gender")
    private Gender ownerGender;

    // 헬스장 기본 정보
    @Column(name = "gym_name", length = 200, nullable = false)
    private String gymName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gym_type")
    @Builder.Default
    private GymType gymType = GymType.INDIVIDUAL;

    @Column(name = "franchise_name", length = 100)
    private String franchiseName;

    // 사업자 인증용
    @Column(name = "business_number", length = 12, unique = true)
    private String businessNumber;

    @Column(name = "business_registration_file", length = 500)
    private String businessRegistrationFile;

    // 계정 상태 & 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "partner_status")
    @Builder.Default
    private PartnerStatus partnerStatus = PartnerStatus.PENDING;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "login_fail_count")
    @Builder.Default
    private Integer loginFailCount = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // Enums
    public enum Gender {
        MALE, FEMALE
    }

    public enum GymType {
        INDIVIDUAL, FRANCHISE
    }

    public enum PartnerStatus {
        PENDING, ACTIVE, SUSPENDED, WITHDRAWN
    }
}
