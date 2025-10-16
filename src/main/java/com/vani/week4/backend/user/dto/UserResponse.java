package com.vani.week4.backend.user.dto;

import com.vani.week4.backend.user.entity.User;
import lombok.*;

/**
 * @author vani
 * @since 10/14/25
 */
@Getter
@Builder
public class UserResponse {
    private String nickname;
    private String profileImageKey;

    public static UserResponse of(User user){
        return UserResponse.builder()
                .nickname(user.getNickname())
                .profileImageKey(user.getProfileImageKey())
                .build();
    }
}
