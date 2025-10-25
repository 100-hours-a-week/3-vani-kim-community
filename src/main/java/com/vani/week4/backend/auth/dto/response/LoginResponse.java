package com.vani.week4.backend.auth.dto.response;

import com.vani.week4.backend.user.entity.User;

/**
 * XXX : SignUpResponse와 거의 동일하니 잘 조절하면 합칠 수 있을듯
 * @author vani
 * @since 10/13/25
 */
public record LoginResponse (
        String accessToken,
        String refreshToken,
        String nickname
) {
    public static LoginResponse of(
            String accessToken,
            String refreshToken,
            String nickname
    ) {
        return new LoginResponse(accessToken, refreshToken, nickname);
    }
}