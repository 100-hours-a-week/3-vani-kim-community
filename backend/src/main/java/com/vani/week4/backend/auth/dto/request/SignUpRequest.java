package com.vani.week4.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author vani
 * @since 10/10/25
 *
 * 유저 생성용 클래스
 * record 타입 이용, Lombok과의 장단 비교
 */
public record SignUpRequest(
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    String password,

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 1, max = 10, message = "닉네임은 1자이상 10자 이하여야합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 가능합니다")
    String nickname,

    //TODO : 본인의 키인지 검증하는 로직 필요할 수 있음.
    String profileImageKey
)
{}
