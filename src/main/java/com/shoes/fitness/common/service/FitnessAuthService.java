package com.shoes.fitness.common.service;


import com.shoes.fitness.common.dto.FileUploadResult;
import com.shoes.fitness.common.dto.FitnessLoginRequest;
import com.shoes.fitness.common.dto.FitnessLoginResponse;
import com.shoes.fitness.common.dto.FitnessRegisterRequest;
import com.shoes.fitness.common.repository.CommonFileRepository;
import com.shoes.fitness.common.repository.FitnessPartnerRepository;
import com.shoes.fitness.common.util.JwtUtil;
import com.shoes.fitness.common.util.UuidUtil;
import com.shoes.fitness.entity.CommonFile;
import com.shoes.fitness.entity.FitnessPartner;
import com.shoes.fitness.entity.RefreshFitnessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FitnessAuthService {

    private final FitnessPartnerRepository fitnessPartnerRepository;
    private final CommonFileRepository commonFileRepository;
    private final RefreshFitnessTokenService refreshFitnessTokenService;
    private final FitnessPartnerLogService fitnessPartnerLogService;
    private final FileUploadService fileUploadService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    /**
     * 피트니스 파트너 회원가입
     */
    @Transactional
    public FitnessLoginResponse register(FitnessRegisterRequest request, String clientIp) {
        // 중복 확인
        if (fitnessPartnerRepository.existsByFitnessLoginId(request.getFitnessLoginId())) {
            throw new RuntimeException("이미 사용 중인 로그인 ID입니다.");
        }
        if (fitnessPartnerRepository.existsByOwnerEmail(request.getOwnerEmail())) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }
        if (request.getBusinessNumber() != null &&
            fitnessPartnerRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            throw new RuntimeException("이미 등록된 사업자등록번호입니다.");
        }

        // 피트니스 파트너 생성
        FitnessPartner fitnessPartner = FitnessPartner.builder()
                .fitnessId(UUID.randomUUID().toString().replace("-", ""))
                .fitnessLoginId(request.getFitnessLoginId())
                .fitnessPassword(passwordEncoder.encode(request.getPassword()))
                .ownerName(request.getOwnerName())
                .ownerPhone(request.getOwnerPhone())
                .ownerEmail(request.getOwnerEmail())
                .ownerBirthDate(request.getOwnerBirthDate())
                .ownerGender(request.getOwnerGender() != null ?
                    FitnessPartner.Gender.valueOf(request.getOwnerGender()) : null)
                .gymName(request.getGymName())
                .gymType(request.getGymType() != null ?
                    FitnessPartner.GymType.valueOf(request.getGymType()) : FitnessPartner.GymType.INDIVIDUAL)
                .franchiseName(request.getFranchiseName())
                .businessNumber(request.getBusinessNumber())
                .partnerStatus(FitnessPartner.PartnerStatus.ACTIVE)
                .build();

        fitnessPartnerRepository.save(fitnessPartner);
        log.info("피트니스 파트너 회원가입 완료: fitnessId={}, loginId={}",
                fitnessPartner.getFitnessId(), fitnessPartner.getFitnessLoginId());

        // 자동 로그인 처리
        return generateLoginResponse(fitnessPartner);
    }

    /**
     * 피트니스 파트너 회원가입 (파일 업로드 포함)
     */
    @Transactional
    public FitnessLoginResponse registerWithFile(FitnessRegisterRequest request,
                                                  MultipartFile businessRegistrationFile,
                                                  String clientIp) {
        // 중복 확인
        if (fitnessPartnerRepository.existsByFitnessLoginId(request.getFitnessLoginId())) {
            throw new RuntimeException("이미 사용 중인 로그인 ID입니다.");
        }
        if (fitnessPartnerRepository.existsByOwnerEmail(request.getOwnerEmail())) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }
        if (request.getBusinessNumber() == null || request.getBusinessNumber().isEmpty()) {
            throw new RuntimeException("사업자등록번호는 필수입니다.");
        }
        if (fitnessPartnerRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            throw new RuntimeException("이미 등록된 사업자등록번호입니다.");
        }

        String fitnessId = UUID.randomUUID().toString().replace("-", "");

        // 피트니스 파트너 생성
        FitnessPartner fitnessPartner = FitnessPartner.builder()
                .fitnessId(fitnessId)
                .fitnessLoginId(request.getFitnessLoginId())
                .fitnessPassword(passwordEncoder.encode(request.getPassword()))
                .ownerName(request.getOwnerName())
                .ownerPhone(request.getOwnerPhone())
                .ownerEmail(request.getOwnerEmail())
                .ownerBirthDate(request.getOwnerBirthDate())
                .ownerGender(request.getOwnerGender() != null && !request.getOwnerGender().isEmpty() ?
                    FitnessPartner.Gender.valueOf(request.getOwnerGender()) : null)
                .gymName(request.getGymName())
                .gymType(request.getGymType() != null && !request.getGymType().isEmpty() ?
                    FitnessPartner.GymType.valueOf(request.getGymType()) : FitnessPartner.GymType.INDIVIDUAL)
                .franchiseName(request.getFranchiseName())
                .businessNumber(request.getBusinessNumber())
                .partnerStatus(FitnessPartner.PartnerStatus.ACTIVE)
                .build();

        fitnessPartnerRepository.save(fitnessPartner);

        // 사업자등록증 파일 업로드 처리 (필수)
        if (businessRegistrationFile == null || businessRegistrationFile.isEmpty()) {
            throw new RuntimeException("사업자등록증 파일은 필수입니다.");
        }

        try {
                String directory = "fitness/" + fitnessId + "/business";
                FileUploadResult uploadResult = fileUploadService.uploadImageFile(businessRegistrationFile, directory);

                // CommonFile 엔티티 저장
                CommonFile commonFile = CommonFile.builder()
                        .id(UuidUtil.generateShortUuid())
                        .partnerId(fitnessId)
                        .fileType(CommonFile.FileType.BUSINESS_LICENSE)
                        .fileName(uploadResult.getFileName())
                        .originalFileName(businessRegistrationFile.getOriginalFilename())
                        .ncpBucket(uploadResult.getBucket())
                        .ncpKey(uploadResult.getKey())
                        .fileUrl(uploadResult.getFileUrl())
                        .fileSize(uploadResult.getFileSize())
                        .fileTypeMime(uploadResult.getContentType())
                        .uploadStatus(CommonFile.UploadStatus.COMPLETED)
                        .isVerified(false)
                        .uploadedBy(fitnessId)
                        .build();

                commonFileRepository.save(commonFile);

                // 임시 파일 정리
                if (uploadResult.getTempFilePath() != null) {
                    fileUploadService.cleanupTempFile(uploadResult.getTempFilePath());
                }

            log.info("사업자등록증 파일 업로드 완료: fitnessId={}, fileId={}", fitnessId, commonFile.getId());
        } catch (Exception e) {
            log.error("사업자등록증 파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("사업자등록증 파일 업로드에 실패했습니다: " + e.getMessage());
        }

        log.info("피트니스 파트너 회원가입 완료 (with file): fitnessId={}, loginId={}",
                fitnessPartner.getFitnessId(), fitnessPartner.getFitnessLoginId());

        // 자동 로그인 처리
        return generateLoginResponse(fitnessPartner);
    }

    /**
     * 피트니스 파트너 로그인
     */
    @Transactional
    public FitnessLoginResponse login(FitnessLoginRequest request, String clientIp, String userAgent) {
        FitnessPartner fitnessPartner = fitnessPartnerRepository.findByFitnessLoginId(request.getFitnessLoginId())
                .orElseThrow(() -> {
                    fitnessPartnerLogService.logLoginFailure(
                            request.getFitnessLoginId(), "존재하지 않는 계정", clientIp, userAgent);
                    return new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
                });

        // 계정 잠금 확인
        if (fitnessPartner.getAccountLockedUntil() != null &&
            fitnessPartner.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            fitnessPartnerLogService.logLoginFailure(
                    fitnessPartner.getFitnessId(), "계정 잠금 상태", clientIp, userAgent);
            throw new RuntimeException("계정이 잠금 상태입니다. " +
                    fitnessPartner.getAccountLockedUntil() + " 이후에 다시 시도해주세요.");
        }

        // 계정 상태 확인
        if (fitnessPartner.getPartnerStatus() == FitnessPartner.PartnerStatus.SUSPENDED) {
            fitnessPartnerLogService.logLoginFailure(
                    fitnessPartner.getFitnessId(), "정지된 계정", clientIp, userAgent);
            throw new RuntimeException("정지된 계정입니다. 관리자에게 문의해주세요.");
        }

        if (fitnessPartner.getPartnerStatus() == FitnessPartner.PartnerStatus.WITHDRAWN) {
            fitnessPartnerLogService.logLoginFailure(
                    fitnessPartner.getFitnessId(), "탈퇴한 계정", clientIp, userAgent);
            throw new RuntimeException("탈퇴한 계정입니다.");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getFitnessPassword(), fitnessPartner.getFitnessPassword())) {
            handleLoginFailure(fitnessPartner, clientIp, userAgent);
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 처리
        fitnessPartner.setLoginFailCount(0);
        fitnessPartner.setAccountLockedUntil(null);
        fitnessPartner.setLastLoginAt(LocalDateTime.now());
        fitnessPartnerRepository.save(fitnessPartner);

        fitnessPartnerLogService.logLoginSuccess(fitnessPartner.getFitnessId(), clientIp, userAgent);
        log.info("피트니스 파트너 로그인 성공: fitnessId={}", fitnessPartner.getFitnessId());

        return generateLoginResponse(fitnessPartner);
    }

    /**
     * 리프레시 토큰으로 액세스 토큰 갱신
     */
    @Transactional
    public FitnessLoginResponse refreshToken(String refreshToken) {
        RefreshFitnessToken token = refreshFitnessTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

        if (!refreshFitnessTokenService.isTokenValid(token)) {
            refreshFitnessTokenService.deleteByToken(refreshToken);
            throw new RuntimeException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        FitnessPartner fitnessPartner = fitnessPartnerRepository.findById(token.getFitnessId())
                .orElseThrow(() -> new RuntimeException("피트니스 파트너를 찾을 수 없습니다."));

        // 토큰 갱신
        RefreshFitnessToken newToken = refreshFitnessTokenService.refreshToken(token);

        // 새 액세스 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(fitnessPartner.getFitnessLoginId());

        return FitnessLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessExpirationTime() / 1000)
                .fitnessPartner(buildFitnessPartnerInfo(fitnessPartner))
                .build();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String fitnessId) {
        refreshFitnessTokenService.deleteByFitnessId(fitnessId);
        fitnessPartnerLogService.logLogout(fitnessId);
        log.info("피트니스 파트너 로그아웃: fitnessId={}", fitnessId);
    }

    /**
     * 로그인 ID 중복 확인
     */
    public boolean isLoginIdAvailable(String loginId) {
        return !fitnessPartnerRepository.existsByFitnessLoginId(loginId);
    }

    /**
     * 이메일 중복 확인
     */
    public boolean isEmailAvailable(String email) {
        return !fitnessPartnerRepository.existsByOwnerEmail(email);
    }

    /**
     * 사업자등록번호 중복 확인
     */
    public boolean isBusinessNumberAvailable(String businessNumber) {
        return !fitnessPartnerRepository.existsByBusinessNumber(businessNumber);
    }

    private void handleLoginFailure(FitnessPartner fitnessPartner, String clientIp, String userAgent) {
        int failCount = fitnessPartner.getLoginFailCount() + 1;
        fitnessPartner.setLoginFailCount(failCount);

        if (failCount >= MAX_LOGIN_ATTEMPTS) {
            fitnessPartner.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            fitnessPartnerLogService.logAccountLock(
                    fitnessPartner.getFitnessId(),
                    "로그인 실패 " + MAX_LOGIN_ATTEMPTS + "회 초과",
                    LOCK_DURATION_MINUTES);
            log.warn("계정 잠금: fitnessId={}, 실패 횟수={}", fitnessPartner.getFitnessId(), failCount);
        }

        fitnessPartnerRepository.save(fitnessPartner);
        fitnessPartnerLogService.logLoginFailure(
                fitnessPartner.getFitnessId(), "비밀번호 불일치", clientIp, userAgent);
    }

    private FitnessLoginResponse generateLoginResponse(FitnessPartner fitnessPartner) {
        String accessToken = jwtUtil.generateAccessToken(fitnessPartner.getFitnessLoginId());
        RefreshFitnessToken refreshToken = refreshFitnessTokenService.createRefreshToken(fitnessPartner.getFitnessId());

        return FitnessLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessExpirationTime() / 1000)
                .fitnessPartner(buildFitnessPartnerInfo(fitnessPartner))
                .build();
    }

    private FitnessLoginResponse.FitnessPartnerInfo buildFitnessPartnerInfo(FitnessPartner fitnessPartner) {
        return FitnessLoginResponse.FitnessPartnerInfo.builder()
                .fitnessId(fitnessPartner.getFitnessId())
                .fitnessLoginId(fitnessPartner.getFitnessLoginId())
                .ownerName(fitnessPartner.getOwnerName())
                .ownerEmail(fitnessPartner.getOwnerEmail())
                .ownerPhone(fitnessPartner.getOwnerPhone())
                .gymName(fitnessPartner.getGymName())
                .gymType(fitnessPartner.getGymType() != null ? fitnessPartner.getGymType().name() : null)
                .franchiseName(fitnessPartner.getFranchiseName())
                .partnerStatus(fitnessPartner.getPartnerStatus().name())
                .build();
    }
}
