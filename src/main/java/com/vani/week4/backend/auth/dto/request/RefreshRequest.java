package com.vani.week4.backend.auth.dto.request;

/**
 * @author vani
 * @since 10/13/25
 */
public record RefreshRequest (
    String refreshToken
) {}
