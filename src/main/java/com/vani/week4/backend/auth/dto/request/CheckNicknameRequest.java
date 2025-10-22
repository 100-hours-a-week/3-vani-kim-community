package com.vani.week4.backend.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author vani
 * @since 10/22/25
 */
public record CheckNicknameRequest(
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 1, max = 10, message = "닉네임은 1자이상 10자 이하여야합니다")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 가능합니다")
        String nickname
) {}
