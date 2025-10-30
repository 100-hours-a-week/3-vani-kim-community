package com.vani.week4.backend.auth;

import com.vani.week4.backend.auth.dto.request.*;
import com.vani.week4.backend.auth.dto.response.LoginResponse;
import com.vani.week4.backend.auth.dto.response.SignUpResponse;
import com.vani.week4.backend.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

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

    // 로그인/sessionId 발급
    @PostMapping("/session")
    public ResponseEntity<String> login(
            HttpServletResponse response,
            @Valid @RequestBody LoginRequest request
    ){
        LoginResponse responseData = authService.login(request);
        Cookie cookie = new Cookie("JSESSIONID", responseData.sessionId());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
//        cookie.setMaxAge(7 * 24 * 60 * 60); // 세션 시간 30분, 종료시 로그아웃되는 일반적인 방식
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseData.nickname());
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response){
        Optional<String> sessionId = extractSessionIdFromCookie(request);

        sessionId.ifPresent(authService::logout);

        Cookie cookie = new Cookie("JSESSIONID", null); // 1. 값을 null로
        cookie.setMaxAge(0); // 2. 만료 시간을 0으로
        cookie.setPath("/"); // 3. 쿠키를 생성했던 경로와 일치해야 함
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }

    //이메일 중복확인
    @GetMapping("/email")
    public ResponseEntity<?> checkDuplicatedEmail(
            @Valid CheckEmailRequest request) {
         authService.checkDuplicatedEmail(request);
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }

    //닉네임 중복확인
    @GetMapping("/nickname")
    public ResponseEntity<?> checkDuplicatedNickname(
            @Valid CheckNicknameRequest request) {
        authService.checkDuplicatedNickname(request);
        return ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }

    private Optional<String> extractSessionIdFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
    // TODO : 이메일 인증
//    @PostMapping("/email")
    // 발송
    // 확인
}
