package com.vani.week4.backend.auth;

import com.vani.week4.backend.auth.dto.request.*;
import com.vani.week4.backend.auth.dto.response.LoginResponse;
import com.vani.week4.backend.auth.dto.response.TokenResponse;
import com.vani.week4.backend.auth.dto.response.SignUpResponse;
import com.vani.week4.backend.auth.service.AuthService;
import com.vani.week4.backend.global.CurrentUser;
import com.vani.week4.backend.user.dto.UserResponse;
import com.vani.week4.backend.user.dto.UserUpdateRequest;
import com.vani.week4.backend.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author vani
 * @since 10/8/25
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    // 회원가입
    @PostMapping("/users")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request){
        SignUpResponse response = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 로그인/토큰 발급
    @PostMapping("/tokens")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //TODO 로그아웃
    //토큰 무효화 필요시
//    @PostMapping

    //토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request){
        TokenResponse response = authService.reissueTokens(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    // 회원 탈퇴
    @PatchMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @CurrentUser String userId,
            @Valid @RequestBody WithdrawRequest request) {

        authService.withdraw(userId, request);
        return ResponseEntity.noContent().build();
    }

    // TODO : 비밀번호 관련 로직
    // 재설정
    @PatchMapping("password")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @CurrentUser User user,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateUser(user, request);

        return ResponseEntity.ok(response);
    }
    // TODO : 이메일 중복 확인
    @GetMapping("/email")
    public ResponseEntity<Boolean> checkDuplicatedEmail(
            @Valid @RequestBody CheckEmailRequest request) {

        return ResponseEntity.ok(authService.checkDuplicatedEmail(request));
    }

    @GetMapping("/nickname")
    public ResponseEntity<Boolean> checkDuplicatedNickname(
            @Valid @RequestBody CheckNicknameRequest request) {

        return ResponseEntity.ok( authService.checkDuplicatedNickname(request));
    }

    // TODO : 이메일 인증
//    @PostMapping("/email")
    // 발송
    // 확인



}
