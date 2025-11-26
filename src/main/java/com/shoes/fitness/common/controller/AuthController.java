package com.shoes.fitness.common.controller;

import com.shoes.fitness.common.dto.*;
import com.shoes.fitness.common.security.CurrentUser;
import com.shoes.fitness.common.security.UserPrincipal;
import com.shoes.fitness.common.service.FitnessAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fitness/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final FitnessAuthService fitnessAuthService;

    /**
     * 피트니스 파트너 회원가입 (JSON)
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<FitnessLoginResponse>> register(
            @Valid @RequestBody FitnessRegisterRequest request,
            HttpServletRequest httpRequest) {

        try {
            String clientIp = getClientIp(httpRequest);
            log.info("피트니스 파트너 회원가입 시도: {} (IP: {})", request.getFitnessLoginId(), clientIp);

            FitnessLoginResponse response = fitnessAuthService.register(request, clientIp);

            return ResponseEntity.ok(ApiResponse.success("회원가입 성공", response));

        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<FitnessLoginResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * 피트니스 파트너 회원가입 (Multipart Form Data - 파일 업로드 포함)
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FitnessLoginResponse>> registerWithFile(
            @RequestParam("fitnessLoginId") String fitnessLoginId,
            @RequestParam("fitnessPassword") String fitnessPassword,
            @RequestParam("ownerName") String ownerName,
            @RequestParam("ownerPhone") String ownerPhone,
            @RequestParam("ownerEmail") String ownerEmail,
            @RequestParam(value = "ownerBirthDate", required = false) String ownerBirthDate,
            @RequestParam(value = "ownerGender", required = false) String ownerGender,
            @RequestParam("gymName") String gymName,
            @RequestParam("gymType") String gymType,
            @RequestParam(value = "franchiseName", required = false) String franchiseName,
            @RequestParam("businessNumber") String businessNumber,
            @RequestParam("businessRegistrationFile") MultipartFile businessRegistrationFile,
            HttpServletRequest httpRequest) {

        try {
            String clientIp = getClientIp(httpRequest);
            log.info("피트니스 파트너 회원가입 시도 (multipart): {} (IP: {})", fitnessLoginId, clientIp);

            // Request 객체 생성
            FitnessRegisterRequest request = FitnessRegisterRequest.builder()
                    .fitnessLoginId(fitnessLoginId)
                    .password(fitnessPassword)
                    .ownerName(ownerName)
                    .ownerPhone(ownerPhone)
                    .ownerEmail(ownerEmail)
                    .ownerBirthDate(ownerBirthDate != null && !ownerBirthDate.isEmpty() ? LocalDate.parse(ownerBirthDate) : null)
                    .ownerGender(ownerGender)
                    .gymName(gymName)
                    .gymType(gymType)
                    .franchiseName(franchiseName)
                    .businessNumber(businessNumber)
                    .build();

            // 파일 포함 회원가입 처리
            FitnessLoginResponse response = fitnessAuthService.registerWithFile(request, businessRegistrationFile, clientIp);

            return ResponseEntity.ok(ApiResponse.success("회원가입 성공", response));

        } catch (Exception e) {
            log.error("회원가입 실패 (multipart): {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<FitnessLoginResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * 피트니스 파트너 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<FitnessLoginResponse>> login(
            @Valid @RequestBody FitnessLoginRequest request,
            HttpServletRequest httpRequest) {

        try {
            String clientIp = getClientIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            log.info("피트니스 파트너 로그인 시도: {} (IP: {})", request.getFitnessLoginId(), clientIp);

            FitnessLoginResponse response = fitnessAuthService.login(request, clientIp, userAgent);

            return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));

        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<FitnessLoginResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * 리프레시 토큰으로 액세스 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<FitnessLoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        try {
            log.info("토큰 갱신 요청");

            FitnessLoginResponse response = fitnessAuthService.refreshToken(request.getRefreshToken());

            return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response));

        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<FitnessLoginResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * 현재 인증 상태 확인
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<FitnessLoginResponse.FitnessPartnerInfo>> checkAuth(
            @CurrentUser UserPrincipal userPrincipal) {

        try {
            if (userPrincipal == null) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.<FitnessLoginResponse.FitnessPartnerInfo>builder()
                                .success(false)
                                .message("인증되지 않은 사용자")
                                .data(null)
                                .build());
            }

            FitnessLoginResponse.FitnessPartnerInfo info = FitnessLoginResponse.FitnessPartnerInfo.builder()
                    .fitnessId(userPrincipal.getFitnessId())
                    .fitnessLoginId(userPrincipal.getFitnessLoginId())
                    .ownerName(userPrincipal.getOwnerName())
                    .ownerEmail(userPrincipal.getOwnerEmail())
                    .gymName(userPrincipal.getGymName())
                    .partnerStatus(userPrincipal.getStatus().name())
                    .build();

            return ResponseEntity.ok(ApiResponse.success("인증 확인", info));

        } catch (Exception e) {
            log.error("인증 확인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<FitnessLoginResponse.FitnessPartnerInfo>builder()
                            .success(false)
                            .message("인증 확인 실패")
                            .data(null)
                            .build());
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CurrentUser UserPrincipal userPrincipal) {
        try {
            if (userPrincipal != null) {
                fitnessAuthService.logout(userPrincipal.getFitnessId());
                log.info("피트니스 파트너 로그아웃 완료: {}", userPrincipal.getFitnessId());
            }
            return ResponseEntity.ok(ApiResponse.success("로그아웃 완료", null));
        } catch (Exception e) {
            log.error("로그아웃 실패: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success("로그아웃 완료", null));
        }
    }

    /**
     * 로그인 ID 중복 확인
     */
    @GetMapping({"/check-login-id/{loginId}", "/check-fitness-login-id/{loginId}"})
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkLoginId(
            @PathVariable String loginId) {

        try {
            boolean isAvailable = fitnessAuthService.isLoginIdAvailable(loginId);

            Map<String, Boolean> result = new HashMap<>();
            result.put("available", isAvailable);

            String message = isAvailable ? "사용 가능한 로그인 ID입니다." : "이미 사용 중인 로그인 ID입니다.";
            return ResponseEntity.ok(ApiResponse.success(message, result));

        } catch (Exception e) {
            log.error("로그인 ID 중복 확인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("중복 확인 중 오류가 발생했습니다."));
        }
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(
            @RequestParam String email) {

        try {
            boolean isAvailable = fitnessAuthService.isEmailAvailable(email);

            Map<String, Boolean> result = new HashMap<>();
            result.put("available", isAvailable);

            String message = isAvailable ? "사용 가능한 이메일입니다." : "이미 등록된 이메일입니다.";
            return ResponseEntity.ok(ApiResponse.success(message, result));

        } catch (Exception e) {
            log.error("이메일 중복 확인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("중복 확인 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사업자등록번호 중복 확인
     */
    @GetMapping("/check-business-number/{businessNumber}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkBusinessNumber(
            @PathVariable String businessNumber) {

        try {
            boolean isAvailable = fitnessAuthService.isBusinessNumberAvailable(businessNumber);

            Map<String, Boolean> result = new HashMap<>();
            result.put("available", isAvailable);

            String message = isAvailable ? "사용 가능한 사업자등록번호입니다." : "이미 등록된 사업자등록번호입니다.";
            return ResponseEntity.ok(ApiResponse.success(message, result));

        } catch (Exception e) {
            log.error("사업자등록번호 중복 확인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("중복 확인 중 오류가 발생했습니다."));
        }
    }

    private String getClientIp(HttpServletRequest request) {
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
