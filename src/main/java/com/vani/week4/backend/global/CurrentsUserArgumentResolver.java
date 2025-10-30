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
 * @author vani
 * @since 10/28/25
 */
@Component
@RequiredArgsConstructor
public class CurrentsUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserService userService;
    private final UserRepository userRepository;



    // 리솔버를 적용할 파라메터인지 겁사
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentUser 어노테이션 붙어 있는지 확인
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    // 파라미터에 실제 어떤 값을 넣어줄지 결정
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
