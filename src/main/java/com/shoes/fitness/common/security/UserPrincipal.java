package com.shoes.fitness.common.security;

import com.shoes.fitness.entity.FitnessPartner;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security에서 사용할 사용자 인증 정보 클래스
 * UserDetails 인터페이스 구현
 */
@Getter
public class UserPrincipal implements UserDetails {

    private String fitnessId;           // FitnessPartner PK (varchar 32)
    private String fitnessLoginId;      // 로그인 ID
    private String password;
    private String ownerName;           // 운영자 이름
    private String ownerEmail;          // 운영자 이메일
    private String gymName;             // 헬스장 이름
    private FitnessPartner.PartnerStatus status;
    private Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(String fitnessId, String fitnessLoginId, String password,
                         String ownerName, String ownerEmail, String gymName,
                         FitnessPartner.PartnerStatus status,
                         Collection<? extends GrantedAuthority> authorities) {
        this.fitnessId = fitnessId;
        this.fitnessLoginId = fitnessLoginId;
        this.password = password;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.gymName = gymName;
        this.status = status;
        this.authorities = authorities;
    }

    /**
     * FitnessPartner 엔티티로부터 UserPrincipal 생성
     */
    public static UserPrincipal create(FitnessPartner fitnessPartner) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_FITNESS_PARTNER");

        return new UserPrincipal(
            fitnessPartner.getFitnessId(),
            fitnessPartner.getFitnessLoginId(),
            fitnessPartner.getFitnessPassword(),
            fitnessPartner.getOwnerName(),
            fitnessPartner.getOwnerEmail(),
            fitnessPartner.getGymName(),
            fitnessPartner.getPartnerStatus(),
            Collections.singletonList(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return fitnessLoginId;  // Spring Security에서 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != FitnessPartner.PartnerStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == FitnessPartner.PartnerStatus.ACTIVE;
    }
}
