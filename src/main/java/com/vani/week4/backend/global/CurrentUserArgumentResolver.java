package com.vani.week4.backend.global;

import com.vani.week4.backend.user.entity.User;
import com.vani.week4.backend.user.repository.UserRepository;
import com.vani.week4.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

/**
 * @CurrentUser 어노테이션이 붙은 파라미터에 인증된 사용자 정보를 주입하는 ArgumentResolver
 *
 * <p>Spring 필터에서 설정한 authenticatedUserId 속성을 기반으로
 *  실제 User 엔티티를 조회하여 컨트롤러 메서드에 전달합니다.</p>
 *
 * @author vani
 * @since 10/28/25
 */
@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    /**
     * 리솔버를 적용할 파라메터인지 겁사하는 메서드
     * @param parameter 메서드 파라미터 정보
     * @return @CurrentUser 어노테이션이 있다면 true
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentUser 어노테이션 붙어 있는지 확인
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    /**
     * 인증된 사용자 ID로 User엔티티를 조회하여 반환하는 메서드
     * 파라미터에 실제 어떤 값을 넣어줄지 결정
     *
     * @param parameter : 메서드 파라미터 정보
     * @param mavContainer : ModelAndView 컨테이너
     * @param webRequest ; 현재 웹 요청
     * @param binderFactory : 데이터 바인더 팩토리
     * @return 조회된 User 엔티티, 인증되지 않았거나 사용자를 찾을 수 없으면 null
     * */
    //
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        //HTTP 요청을 가져옴
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        //필터가 저장한 Id를 꺼냄
        String userId = (String) request.getAttribute("authenticatedUserId");

        if (userId == null) {
            return null;        //인증 안되었으면
        }

        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }
}
