package com.vani.week4.backend.auth.service;
import com.vani.week4.backend.auth.dto.request.LoginRequest;
import com.vani.week4.backend.auth.dto.request.SignUpRequest;
import com.vani.week4.backend.auth.dto.request.WithdrawRequest;
import com.vani.week4.backend.auth.dto.response.LoginResponse;
import com.vani.week4.backend.auth.dto.response.TokenResponse;
import com.vani.week4.backend.auth.dto.response.SignUpResponse;
import com.vani.week4.backend.auth.entity.Auth;
import com.vani.week4.backend.auth.entity.ProviderType;
import com.vani.week4.backend.auth.repository.AuthRepository;
import com.vani.week4.backend.auth.security.JwtTokenProvider;
import com.vani.week4.backend.global.exception.*;
import com.vani.week4.backend.user.entity.User;
import com.vani.week4.backend.user.entity.UserStatus;
import com.vani.week4.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

/**
 * @author vani
 * @since 10/9/25
 */
// FIXME 토큰 주는 대부분의 response 비슷해서 통합하기
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
//    private final UserInternalApiClient userApiClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String,String> redisTemplate;

    /**
     * 회원가입을 진행하는 메서드
     * @return : 인증용 토큰 발급
     * */
    // TODO : 이메일/닉네임 중복 검증 api 분리 필요
    // TODO : OAUTH, 소셜로그인 도입
    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest){

        if (authRepository.existsByEmail(signUpRequest.email())) {
            throw new EmailAlreadyExistsException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(signUpRequest.nickname())) {
            throw new NicknameAlreadyExistsException("이미 사용 중인 닉네임입니다.");
        }

        // User 생성 요청
        String userId = UlidCreator.getUlid().toString();
        User user = User.createUser(
                userId,
                signUpRequest.nickname(),
                signUpRequest.profileImageKey()
                );
        userRepository.save(user);


        //Auth 생성 요청
        Auth auth = Auth.ceateAuth(
                user,
                userId,
                signUpRequest.email(),
                ProviderType.LOCAL,
                passwordEncoder.encode(signUpRequest.password())
        );
        authRepository.save(auth);

        //토큰생성
        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        return SignUpResponse.of(
                userId,
                accessToken,
                refreshToken
        );
    }

    /**
     * 로그인 로직
     * @return : 인증용 토큰 발급
     * */
    // TODO : 삭제 후 재로그인 전략 필요
    @Transactional
    public LoginResponse login(LoginRequest request) {

        Auth auth = authRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(request.password(), auth.getPasswordHash())){
            throw new InvalidPasswordException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        User user = auth.getUser();
        if(user == null){
            throw new IllegalStateException("인증 정보에 연결된 유저 정보가 없습니다.");
        }

        if (!user.isActive()) {
            throw new UserAccessDeniedException("로그인할 수 없는 계정입니다.");
        }

        String userId = user.getId();

        //토큰생성
        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        // 최종 응답 DTO 반환
        return LoginResponse.of(
                accessToken,
                refreshToken
        );
    }

    /**
     * refresh토큰을 이용하여 access토큰을 재발급 하는 메서드
     * */
    //TODO 레디스 연결 필요
    @Transactional
    public TokenResponse reissueTokens(String refreshToken) {
        //토큰 자체 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)){
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        //레디스에서 리프레시 토큰 조회
        String storedRefreshToken = redisTemplate.opsForValue().get(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
        //새 토큰 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

        //레디스 갱신
        redisTemplate.opsForValue().set(
                userId,
                newRefreshToken,
                Duration.ofDays(14)
        );
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    /**
     * 유저를 소프트 delete하는 메서드
     *
     * */
    // TODO : 실제 삭제를 배치 처리해야함
    // TODO : 토큰 블랙리스트 필요
    @Transactional
    public void withdraw(String userId, WithdrawRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

        Auth auth = authRepository.findByUserAndProvider(user, ProviderType.LOCAL)
                .orElseThrow(() -> new AuthNotFoundException("비밀번호 인증 정보가 없는 유저입니다."));

        if (!passwordEncoder.matches(request.password(), auth.getPasswordHash())) {
            throw new InvalidPasswordException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        if(user == null){
            throw new IllegalStateException("인증 정보에 연결된 유저 정보가 없습니다.");
        }
        user.updateUserStatus(UserStatus.DELETED);

    }
}