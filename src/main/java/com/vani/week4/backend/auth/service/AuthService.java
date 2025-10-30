package com.vani.week4.backend.auth.service;
import com.vani.week4.backend.auth.SessionStore;
import com.vani.week4.backend.auth.dto.request.*;
import com.vani.week4.backend.auth.dto.response.LoginResponse;
import com.vani.week4.backend.auth.dto.response.SignUpResponse;
import com.vani.week4.backend.auth.entity.Auth;
import com.vani.week4.backend.auth.entity.ProviderType;
import com.vani.week4.backend.auth.repository.AuthRepository;
//import com.vani.week4.backend.auth.security.JwtTokenProvider;
import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.*;
import com.vani.week4.backend.user.dto.PasswordUpdateRequest;
import com.vani.week4.backend.user.entity.User;
import com.vani.week4.backend.user.entity.UserStatus;
import com.vani.week4.backend.user.repository.UserRepository;
import com.vani.week4.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final SessionStore sessionStore;
    /**
     * 회원가입을 진행하는 메서드
     * @return : 인증용 토큰 발급
     * */
    // TODO : OAUTH, 소셜로그인 도입
    // TODO : local로 가입시 중복이메일 확인 내부로직
    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest){
        // User 생성 요청
        String userId = UlidCreator.getUlid().toString();

        User user = userService.createUser(userId, signUpRequest);

        //Auth 생성 요청
        Auth auth = Auth.ceateAuth(
                user,
                userId,
                signUpRequest.email(),
                ProviderType.LOCAL,
                passwordEncoder.encode(signUpRequest.password())
        );
        authRepository.save(auth);

        return new SignUpResponse(userId);
    }

    /**
     * 로그인 로직
     * @return : 세션아이디와 닉네임
     * */
    @Transactional
    public LoginResponse login(LoginRequest request) {

        Auth auth = authRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!passwordEncoder.matches(request.password(), auth.getPasswordHash())){
            throw new InvalidPasswordException(ErrorCode.RESOURCE_CONFLICT);
        }
        User user = auth.getUser();
        if(user == null){
            throw new IllegalStateException("인증 정보에 연결된 유저 정보가 없습니다.");
        }

        if(user.isDeleted()){
            user.updateUserStatus(UserStatus.ACTIVE);
        }
        if (!user.isActive()) {
            throw new UserAccessDeniedException(ErrorCode.FORBIDDEN);
        }

        String userId = user.getId();
        String nickname = user.getNickname();
        //세션 ID 생성
        String sessionId = UlidCreator.getUlid().toString();
        sessionStore.saveSessionId(sessionId, userId);

        return new LoginResponse( sessionId, nickname );
    }

    @Transactional
    public void logout(String sessionId){
        sessionStore.removeSessionId(sessionId);


    }

    public void checkDuplicatedEmail(CheckEmailRequest request){
        boolean isDuplicated = authRepository.existsByEmail(request.email());
        if(isDuplicated) {
            throw new EmailAlreadyExistsException(ErrorCode.RESOURCE_CONFLICT);
        }
    }

    public void checkDuplicatedNickname(CheckNicknameRequest request){
        boolean isDuplicated = userRepository.existsByNickname(request.nickname());
        if(isDuplicated) {
            throw new NicknameAlreadyExistsException(ErrorCode.RESOURCE_CONFLICT);
        }
    }

    public boolean checkPassword(User user, String password){
        Auth auth = authRepository.findByUserAndProvider(user, ProviderType.LOCAL)
                .orElseThrow(() -> new AuthNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        String E_PASSWORD = auth.getPasswordHash();
        return passwordEncoder.matches(password, E_PASSWORD);
    }

    //TODO 전에 사용한적 있는 비번 방지
    @Transactional
    public void updatePassword(User user, PasswordUpdateRequest request){
        Auth auth = authRepository.findByUserAndProvider(user, ProviderType.LOCAL)
                .orElseThrow(() -> new AuthNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        auth.setPasswordHash(passwordEncoder.encode(request.password()));
    }

}