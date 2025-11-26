package com.shoes.fitness.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fitness_partner_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessPartnerLog {

    @Id
    @Column(name = "log_id", length = 32)
    private String logId;

    @Column(name = "fitness_id", length = 32, nullable = false)
    private String fitnessId;

    @Column(name = "admin_id", length = 32)
    private String adminId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "action_detail", length = 500)
    private String actionDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private Result result;

    @Column(name = "before_value", columnDefinition = "TEXT")
    private String beforeValue;

    @Column(name = "after_value", columnDefinition = "TEXT")
    private String afterValue;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_id", referencedColumnName = "fitness_id", insertable = false, updatable = false)
    private FitnessPartner fitnessPartner;

    // Enums
    public enum ActionType {
        LOGIN,              // 로그인
        LOGIN_FAIL,         // 로그인 실패
        LOGOUT,             // 로그아웃
        PASSWORD_CHANGE,    // 비밀번호 변경
        INFO_UPDATE,        // 정보 수정
        ACCOUNT_LOCK,       // 계정 잠금
        ACCOUNT_UNLOCK,     // 계정 잠금 해제
        TOKEN_REFRESH,      // 토큰 갱신
        GYM_INFO_UPDATE,    // 헬스장 정보 수정
        BUSINESS_VERIFY,    // 사업자 인증
        WITHDRAW            // 회원 탈퇴
    }

    public enum Result {
        SUCCESS,
        FAIL,
        ERROR
    }

    @PrePersist
    public void prePersist() {
        if (this.logId == null) {
            this.logId = java.util.UUID.randomUUID().toString().replace("-", "");
        }
    }
}
