package com.shoes.fitness.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FitnessRegisterRequest {

    @NotBlank(message = "로그인 ID는 필수입니다.")
    @Size(min = 4, max = 50, message = "로그인 ID는 4~50자 사이여야 합니다.")
    private String fitnessLoginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    // 운영자 정보
    @NotBlank(message = "이름은 필수입니다.")
    private String ownerName;

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String ownerPhone;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String ownerEmail;

    private LocalDate ownerBirthDate;

    private String ownerGender;  // MALE, FEMALE

    // 헬스장 정보
    @NotBlank(message = "헬스장 상호명은 필수입니다.")
    private String gymName;

    private String gymType;  // INDIVIDUAL, FRANCHISE (기본값: INDIVIDUAL)

    private String franchiseName;  // gymType이 FRANCHISE인 경우

    // 사업자 정보
    @Pattern(regexp = "^[0-9]{10,12}$", message = "사업자등록번호는 10~12자리 숫자입니다.")
    private String businessNumber;
}
