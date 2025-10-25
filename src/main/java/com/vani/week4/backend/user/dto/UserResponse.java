package com.vani.week4.backend.user.dto;

import com.vani.week4.backend.user.entity.User;
import lombok.*;

/**
 * @author vani
 * @since 10/14/25
 */
public record UserResponse(
    String nickname,
    String profileImageKey
) {}

