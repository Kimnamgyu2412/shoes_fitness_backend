package com.shoes.fitness.common.security;

import com.shoes.fitness.common.repository.FitnessPartnerRepository;
import com.shoes.fitness.entity.FitnessPartner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security에서 사용할 사용자 인증 정보 로드 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final FitnessPartnerRepository fitnessPartnerRepository;

    /**
     * 사용자명(피트니스 로그인 ID)으로 사용자 정보 로드
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 정보 로드: {}", username);

        FitnessPartner fitnessPartner = fitnessPartnerRepository.findByFitnessLoginId(username)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음: {}", username);
                    return new UsernameNotFoundException("피트니스 파트너를 찾을 수 없습니다: " + username);
                });

        log.debug("사용자 정보 로드 성공: fitnessId={}, status={}",
                fitnessPartner.getFitnessId(), fitnessPartner.getPartnerStatus());

        return UserPrincipal.create(fitnessPartner);
    }

    /**
     * 피트니스 ID로 사용자 정보 로드
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String fitnessId) {
        log.debug("피트니스 ID로 사용자 정보 로드: {}", fitnessId);

        FitnessPartner fitnessPartner = fitnessPartnerRepository.findById(fitnessId)
                .orElseThrow(() -> {
                    log.error("피트니스 파트너를 찾을 수 없음: fitnessId={}", fitnessId);
                    return new UsernameNotFoundException("피트니스 파트너를 찾을 수 없습니다: " + fitnessId);
                });

        return UserPrincipal.create(fitnessPartner);
    }
}
