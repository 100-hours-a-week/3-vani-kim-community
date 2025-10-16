package com.vani.week4.backend.user.service;

import com.vani.week4.backend.global.CurrentUser;
import com.vani.week4.backend.user.dto.UserResponse;
import com.vani.week4.backend.user.dto.UserUpdateRequest;
import com.vani.week4.backend.user.entity.User;
import com.vani.week4.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author vani
 * @since 10/14/25
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse getUserInfo(User user) {
        return UserResponse.builder()
                .nickname(user.getNickname())
                .profileImageKey(user.getProfileImageKey())
                .build();
    }

    @Transactional
    public UserResponse updateUser(User user, UserUpdateRequest request) {

        if (request.nickname() != null) {
            user.updateNickname(request.nickname());
        }

        if (request.profileImageKey() != null) {
            user.updateProfileImageKey(request.profileImageKey());
        }

        return UserResponse.of(user);
    }

}
