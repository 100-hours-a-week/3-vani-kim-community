package com.vani.week4.backend.auth.dto.response;

/**
 * 회원가입 응답 DTO
 * @author vani
 * @since 10/10/25
 *
 */
public record SignUpResponse (
    String userId,
    String accessToken,
    String refreshToken
) {
    public static SignUpResponse of(
            String userId,
            String accessToken,
            String refreshToken
    ) {
        return new SignUpResponse(userId, accessToken, refreshToken);
    }
}
