package com.vani.week4.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author vani
 * @since 10/10/25
 *
 */
public record UserUpdateRequest(
    @Size(min = 1, max = 10, message = "닉네임은 1자이상 10자 이하여야합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 가능합니다")
    String nickname,

    //TODO : 본인의 키인지 검증하는 로직 필요할 수 있음.
    String profileImageKey
) {
    public UserUpdateRequest {
        if (nickname != null && nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 공백일 수 없습니다.");
        }
    }
}
