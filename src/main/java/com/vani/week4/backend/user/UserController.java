package com.vani.week4.backend.user;

import com.vani.week4.backend.global.CurrentUser;
import com.vani.week4.backend.user.dto.UserResponse;
import com.vani.week4.backend.user.dto.UserUpdateRequest;
import com.vani.week4.backend.user.entity.User;
import com.vani.week4.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author vani
 * @since 10/8/25
 */
// TODO 좋아요한 게시글 가져오기
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 생성과 삭제는 Auth에서 담당

    //회원 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@CurrentUser User user) {
        UserResponse userResponse = userService.getUserInfo(user);
        return ResponseEntity.ok(userResponse);
    }
    //회원정보 수정
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @CurrentUser User user,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateUser(user, request);

        return ResponseEntity.ok(response);
    }


}
