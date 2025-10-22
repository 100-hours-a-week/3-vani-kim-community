package com.vani.week4.backend.auth.security;

/**
 * @author vani
 * @since 10/22/25
 */
public record ErrorResponse(
        String code,
        String message
) {
}
