package com.shoes.fitness.common.service;

import com.shoes.fitness.common.repository.RefreshFitnessTokenRepository;
import com.shoes.fitness.common.util.JwtUtil;
import com.shoes.fitness.entity.RefreshFitnessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshFitnessTokenService {

    private final RefreshFitnessTokenRepository refreshFitnessTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${security.jwt.absolute-expiration}")
    private long absoluteExpiration;

    /**
     * 리프레시 토큰 생성 및 저장
     */
    @Transactional
    public RefreshFitnessToken createRefreshToken(String fitnessId) {
        // 기존 토큰 삭제
        refreshFitnessTokenRepository.deleteByFitnessId(fitnessId);

        String tokenValue = jwtUtil.generateRefreshToken(fitnessId);
        LocalDateTime now = LocalDateTime.now();

        RefreshFitnessToken refreshToken = RefreshFitnessToken.builder()
                .id(UUID.randomUUID().toString().replace("-", ""))
                .token(tokenValue)
                .fitnessId(fitnessId)
                .expiryDate(now.plusSeconds(refreshExpiration))
                .absoluteExpiryDate(now.plusSeconds(absoluteExpiration))
                .refreshCount(0)
                .build();

        refreshFitnessTokenRepository.save(refreshToken);
        log.info("리프레시 토큰 생성: fitnessId={}", fitnessId);

        return refreshToken;
    }

    /**
     * 토큰으로 리프레시 토큰 조회
     */
    @Transactional(readOnly = true)
    public Optional<RefreshFitnessToken> findByToken(String token) {
        return refreshFitnessTokenRepository.findByToken(token);
    }

    /**
     * 리프레시 토큰 갱신
     */
    @Transactional
    public RefreshFitnessToken refreshToken(RefreshFitnessToken existingToken) {
        // 절대 만료 시간 체크
        if (existingToken.isAbsoluteExpired()) {
            log.warn("절대 만료 시간 초과: fitnessId={}", existingToken.getFitnessId());
            refreshFitnessTokenRepository.delete(existingToken);
            throw new RuntimeException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
        }

        // 새 토큰 생성
        String newTokenValue = jwtUtil.generateRefreshToken(existingToken.getFitnessId());
        existingToken.setToken(newTokenValue);
        existingToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshExpiration));
        existingToken.incrementRefreshCount();

        refreshFitnessTokenRepository.save(existingToken);
        log.info("리프레시 토큰 갱신: fitnessId={}, refreshCount={}",
                existingToken.getFitnessId(), existingToken.getRefreshCount());

        return existingToken;
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean isTokenValid(RefreshFitnessToken token) {
        return !token.isExpired() && !token.isAbsoluteExpired();
    }

    /**
     * 피트니스 ID로 토큰 삭제 (로그아웃)
     */
    @Transactional
    public void deleteByFitnessId(String fitnessId) {
        refreshFitnessTokenRepository.deleteByFitnessId(fitnessId);
        log.info("리프레시 토큰 삭제: fitnessId={}", fitnessId);
    }

    /**
     * 토큰 값으로 삭제
     */
    @Transactional
    public void deleteByToken(String token) {
        refreshFitnessTokenRepository.deleteByToken(token);
    }

    /**
     * 만료된 토큰 정리
     */
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshFitnessTokenRepository.deleteExpiredTokens(now);
        refreshFitnessTokenRepository.deleteAbsoluteExpiredTokens(now);
        log.info("만료된 리프레시 토큰 정리 완료");
    }
}
