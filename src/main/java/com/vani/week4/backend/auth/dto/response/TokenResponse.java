package com.vani.week4.backend.auth.dto.response;

/**
 * @author vani
 * @since 10/13/25
 */
public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}