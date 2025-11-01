package com.vani.week4.backend.auth;

import com.vani.week4.backend.auth.dto.request.*;
import com.vani.week4.backend.auth.dto.response.LoginResponse;
import com.vani.week4.backend.auth.dto.response.TokenResponse;
import com.vani.week4.backend.auth.dto.response.SignUpResponse;
import com.vani.week4.backend.auth.service.AuthService;
import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.InvalidTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * 인증/인가를 담당하는 컨트롤러
 * @author vani
 * @since 10/8/25
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private static final int REFRESH_TOKEN_EXPIRATION = 1209600;//14 * 24 * 3600 14일

    // 회원가입
    @PostMapping("/users")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request){
        SignUpResponse response = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 로그인/토큰 발급
    // Access토큰은 보안헤더에, refresh토큰은 쿠키에 담아, 사용자의 닉네입과 함께 반환한다.
    @PostMapping("/tokens")
    public ResponseEntity<Map<String,String>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ){
        LoginResponse loginResponse = authService.login(request);
        response.setHeader("Authorization","Bearer " + loginResponse.accessToken());
        addTokenCookie(
                response, "refreshToken", loginResponse.refreshToken(),
                REFRESH_TOKEN_EXPIRATION, "/auth/refresh"
        );
        Map<String, String> responseBody = Map.of("nickname", loginResponse.nickname());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    //로그아웃
    //서버의 저장하고 있는 인증관련 토큰을 폐기하고 사용자의 토큰용 쿠키를 폐기시킨다.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response){
        Optional<String> refreshToken = extractRefreshTokenFromCookie(request);
        refreshToken.ifPresent(authService::deleteToken);

        addTokenCookie( response, "refreshToken", "",
                0, "/auth/refresh"
        );

        return ResponseEntity.noContent().build();
    }

    //토큰 갱신
    //Refresh 토큰의 유효성을 검사하고 새 Access토큰을 보안 헤더, 새 Refresh 토큰은 쿠키에 반환한다
    //TODO 필터에서 Refresh 토큰도 처리해서 토큰을 꺼내 주는 것이 필요할 듯 하다.
    //TODO 유효성 검사를 여기서 할 필요가 있나??
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ){

        log.info("===== ➡️ /auth/refresh 요청 도착 =====");

        // 쿠키에서 토큰 추출
        String refreshToken = extractRefreshTokenFromCookie(request)
                .orElseThrow(() ->{
                    log.warn("🚨 [AuthController] 쿠키에서 리프레시 토큰을 찾을 수 없습니다!");
                    return new InvalidTokenException(ErrorCode.UNAUTHORIZED);
                    });
        log.info("✅ [AuthController] 쿠키에서 리프레시 토큰 추출 성공");
        //TODO 오류를 확인하기 위해서 추가, 토큰 값이 보이는건 안 좋으니 지워야함.
        log.debug("추출된 토큰 값(일부): {}", refreshToken.substring(0, Math.min(10, refreshToken.length())));

        TokenResponse tokenResponse = authService.reissueTokens(refreshToken);

        //응답헤더와 쿠키 설정
        response.setHeader("Authorization","Bearer " + tokenResponse.accessToken());
        addTokenCookie(
                response, "refreshToken", tokenResponse.refreshToken(),
                REFRESH_TOKEN_EXPIRATION, "/auth/refresh"
        );

        return ResponseEntity.noContent().build();
    }

    //Auth 테이블에 존재하는 이메일 중복 확인.
    @GetMapping("/email")
    public ResponseEntity<?> checkDuplicatedEmail(
            @Valid CheckEmailRequest request) {
         authService.checkDuplicatedEmail(request);
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }

    //Auth 테이블에 존재하는 닉네임 중복확인
    @GetMapping("/nickname")
    public ResponseEntity<?> checkDuplicatedNickname(
            @Valid CheckNicknameRequest request) {
        authService.checkDuplicatedNickname(request);
        return ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }
    /**
     * 공통 토큰 쿠키 생성 로직 메서드
     * httpOnly : True, secure : True, sameSite : None
     * @param response : 최종 응답 response
     * @param  name : 들어갈 토큰 이름
     * @param value : 실제 토큰 값
     * @param maxAge : 쿠키 수명
     * @param path : 쿠키를 허용할 URI
     * */
    // TODO 더 쉽게 처리할 수 있는 전역적인 방법 고려
    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge, String path) {

        ResponseCookie cookie = ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .path(path)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    /**
     * 쿠키에서 리프레시 토큰만을 추출하는 메서드
     * @param request : 서블릿에서 커스텀 필터를 거치지 않고 전달 받은 요청
     * */
    private Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    // TODO : 이메일 인증
//    @PostMapping("/email")
    // 발송
    // 확인
}
