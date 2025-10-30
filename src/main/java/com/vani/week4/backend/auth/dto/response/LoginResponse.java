package com.vani.week4.backend.auth.dto.response;

import com.vani.week4.backend.user.entity.User;

/**
 * @author vani
 * @since 10/13/25
 */
public record LoginResponse (
        String sessionId,
        String nickname
) { }