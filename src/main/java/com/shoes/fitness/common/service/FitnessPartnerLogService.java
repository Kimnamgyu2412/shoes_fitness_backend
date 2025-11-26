package com.shoes.fitness.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoes.fitness.common.repository.FitnessPartnerLogRepository;
import com.shoes.fitness.entity.FitnessPartnerLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FitnessPartnerLogService {

    private final FitnessPartnerLogRepository fitnessPartnerLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * 피트니스 파트너 활동 로그 기록 (비동기)
     */
    @Async
    @Transactional
    public void logActivity(String fitnessId,
                           FitnessPartnerLog.ActionType actionType,
                           String actionDetail,
                           FitnessPartnerLog.Result result,
                           HttpServletRequest request) {
        try {
            FitnessPartnerLog fitnessLog = FitnessPartnerLog.builder()
                    .fitnessId(fitnessId)
                    .actionType(actionType)
                    .actionDetail(actionDetail)
                    .result(result)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .sessionId(request.getSession(false) != null ? request.getSession().getId() : null)
                    .build();

            fitnessPartnerLogRepository.save(fitnessLog);
            log.debug("피트니스 파트너 활동 로그 저장 완료: fitnessId={}, actionType={}, result={}",
                    fitnessId, actionType, result);

        } catch (Exception e) {
            log.error("피트니스 파트너 활동 로그 저장 실패: fitnessId={}, actionType={}, error={}",
                    fitnessId, actionType, e.getMessage(), e);
        }
    }

    /**
     * 로그인 성공 로그
     */
    @Async
    @Transactional
    public void logLoginSuccess(String fitnessId, String ipAddress, String userAgent) {
        try {
            FitnessPartnerLog fitnessLog = FitnessPartnerLog.builder()
                    .fitnessId(fitnessId)
                    .actionType(FitnessPartnerLog.ActionType.LOGIN)
                    .actionDetail("로그인 성공")
                    .result(FitnessPartnerLog.Result.SUCCESS)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            fitnessPartnerLogRepository.save(fitnessLog);
            log.info("로그인 성공 로그 저장: fitnessId={}, ip={}", fitnessId, ipAddress);

        } catch (Exception e) {
            log.error("로그인 성공 로그 저장 실패: fitnessId={}, error={}", fitnessId, e.getMessage());
        }
    }

    /**
     * 로그인 실패 로그
     */
    @Async
    @Transactional
    public void logLoginFailure(String loginId, String reason, String ipAddress, String userAgent) {
        try {
            FitnessPartnerLog fitnessLog = FitnessPartnerLog.builder()
                    .fitnessId(loginId)
                    .actionType(FitnessPartnerLog.ActionType.LOGIN_FAIL)
                    .actionDetail("로그인 실패: " + reason)
                    .result(FitnessPartnerLog.Result.FAIL)
                    .errorMessage(reason)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            fitnessPartnerLogRepository.save(fitnessLog);
            log.warn("로그인 실패 로그 저장: loginId={}, reason={}, ip={}", loginId, reason, ipAddress);

        } catch (Exception e) {
            log.error("로그인 실패 로그 저장 실패: loginId={}, error={}", loginId, e.getMessage());
        }
    }

    /**
     * 로그아웃 로그
     */
    @Async
    @Transactional
    public void logLogout(String fitnessId) {
        try {
            FitnessPartnerLog fitnessLog = FitnessPartnerLog.builder()
                    .fitnessId(fitnessId)
                    .actionType(FitnessPartnerLog.ActionType.LOGOUT)
                    .actionDetail("로그아웃")
                    .result(FitnessPartnerLog.Result.SUCCESS)
                    .build();

            fitnessPartnerLogRepository.save(fitnessLog);
            log.info("로그아웃 로그 저장: fitnessId={}", fitnessId);

        } catch (Exception e) {
            log.error("로그아웃 로그 저장 실패: fitnessId={}, error={}", fitnessId, e.getMessage());
        }
    }

    /**
     * 비밀번호 변경 로그
     */
    @Async
    @Transactional
    public void logPasswordChange(String fitnessId, boolean success, String ipAddress) {
        try {
            FitnessPartnerLog fitnessLog = FitnessPartnerLog.builder()
                    .fitnessId(fitnessId)
                    .actionType(FitnessPartnerLog.ActionType.PASSWORD_CHANGE)
                    .actionDetail(success ? "비밀번호 변경 성공" : "비밀번호 변경 실패")
                    .result(success ? FitnessPartnerLog.Result.SUCCESS : FitnessPartnerLog.Result.FAIL)
                    .ipAddress(ipAddress)
                    .build();

            fitnessPartnerLogRepository.save(fitnessLog);
            log.info("비밀번호 변경 로그 저장: fitnessId={}, success={}", fitnessId, success);

        } catch (Exception e) {
            log.error("비밀번호 변경 로그 저장 실패: fitnessId={}, error={}", fitnessId, e.getMessage());
        }
    }

    /**
     * 계정 잠금 로그
     */
    @Async
    @Transactional
    public void logAccountLock(String fitnessId, String reason, int lockMinutes) {
        try {
            Map<String, Object> details = Map.of(
                    "reason", reason,
                    "lockMinutes", lockMinutes,
                    "lockedUntil", LocalDateTime.now().plusMinutes(lockMinutes)
            );

            String detailJson = objectMapper.writeValueAsString(details);

            FitnessPartnerLog fitnessLog = FitnessPartnerLog.builder()
                    .fitnessId(fitnessId)
                    .actionType(FitnessPartnerLog.ActionType.ACCOUNT_LOCK)
                    .actionDetail("계정 잠금: " + reason)
                    .afterValue(detailJson)
                    .result(FitnessPartnerLog.Result.SUCCESS)
                    .build();

            fitnessPartnerLogRepository.save(fitnessLog);
            log.warn("계정 잠금 로그 저장: fitnessId={}, reason={}, lockMinutes={}",
                    fitnessId, reason, lockMinutes);

        } catch (Exception e) {
            log.error("계정 잠금 로그 저장 실패: fitnessId={}, error={}", fitnessId, e.getMessage());
        }
    }

    /**
     * 정보 변경 로그
     */
    @Async
    @Transactional
    public void logInfoChange(String fitnessId,
                             FitnessPartnerLog.ActionType actionType,
                             String actionDetail,
                             Object beforeValue,
                             Object afterValue,
                             FitnessPartnerLog.Result result,
                             HttpServletRequest request) {
        try {
            String beforeJson = objectMapper.writeValueAsString(beforeValue);
            String afterJson = objectMapper.writeValueAsString(afterValue);

            FitnessPartnerLog fitnessLog = FitnessPartnerLog.builder()
                    .fitnessId(fitnessId)
                    .actionType(actionType)
                    .actionDetail(actionDetail)
                    .beforeValue(beforeJson)
                    .afterValue(afterJson)
                    .result(result)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .sessionId(request.getSession(false) != null ? request.getSession().getId() : null)
                    .build();

            fitnessPartnerLogRepository.save(fitnessLog);
            log.debug("정보 변경 로그 저장 완료: fitnessId={}, actionType={}, result={}",
                    fitnessId, actionType, result);

        } catch (JsonProcessingException e) {
            log.error("JSON 변환 실패: fitnessId={}, actionType={}, error={}",
                    fitnessId, actionType, e.getMessage(), e);
        } catch (Exception e) {
            log.error("정보 변경 로그 저장 실패: fitnessId={}, actionType={}, error={}",
                    fitnessId, actionType, e.getMessage(), e);
        }
    }

    /**
     * 최근 로그인 실패 횟수 조회
     */
    public long getRecentLoginFailureCount(String fitnessId, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return fitnessPartnerLogRepository.countRecentLoginFailures(fitnessId, since);
    }

    /**
     * 클라이언트 IP 추출
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
