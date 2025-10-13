package com.vani.week4.backend.auth.dto;

import com.vani.week4.backend.user.entity.User;

/**
 * @author vani
 * @since 10/10/25
 */
public class SignUpResponse (
    String id,
    String email,
    String nickname,
    String createAt
) {
    public static SignUpResponse from(User user){
        return new SignUpResponse(
                user.getId(),
                user.getEmail(),
                user.get
        )
    }
}
