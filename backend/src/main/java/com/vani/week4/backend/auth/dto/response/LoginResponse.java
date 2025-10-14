package com.vani.week4.backend.auth.dto.response;

/**
 * XXX : SignUpResponse와 거의 동일하니 잘 조절하면 합칠 수 있을듯
 * @author vani
 * @since 10/13/25
 */
public record LoginResponse (
        String accessToken,
        String refreshToken
) {
    public static LoginResponse of(
            String accessToken,
            String refreshToken
    ) {
        return new LoginResponse(accessToken, refreshToken);
    }
}