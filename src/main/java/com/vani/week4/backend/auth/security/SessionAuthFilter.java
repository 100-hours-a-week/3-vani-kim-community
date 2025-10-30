package com.vani.week4.backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vani.week4.backend.auth.SessionStore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * 헤더 쿠키의 세션 아이디를 검증하는 필터
 * @author vani
 * @since 10/28/25
 */
@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {
    private final SessionStore sessionStore;
    private final ObjectMapper objectMapper;

    //필터 제외 경로 목록
    private static final String[] EXCLUDED_PATHS = {
        "/auth/users", "/auth/session", "/auth/nickname", "/auth/email",
    };

    // 필터 제외 경로 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {

        logger.debug("SessionAuthFilter: doFilterInternal"+ ((HttpServletRequest) request).getRequestURI());
        try {
            Optional<String> sessionId = extractSessionIdFromCookie(request);

            // 세션 아이디 없음 오류. == 만료
            if (sessionId.isEmpty()) {
                setErrorResponse(response, "S001", "Expired SessionId");
                return;
            }

            Optional<String> userId = validateSessionId(sessionId.get());
            //쿠키의 세션id 잘못된 경우, 401
            if (userId.isEmpty()) {
                setErrorResponse(response, "S002", "Invalid SessionId");
                return;
            }
            request.setAttribute("authenticatedUserId", userId.get());
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("SessionAuthFilter: doFilterInternal", e);
        }
    }

    // 쿠키에서 세션아이디 추출
    private Optional<String> extractSessionIdFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    // 세션아이디가 세션 스토어에 존재하는지 검증
    private Optional<String> validateSessionId(String sessionId) {
        try {
            String userId = sessionStore.getUserIdBySessionId(sessionId);
            if(userId == null) {
                return Optional.empty();
            }
            return Optional.of(userId);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }


    /**
     * JSON 에러 응답을 직접 만들어주는 헬퍼 메서드
     * 지금 단계에서 에러는 디스패처 서블렛으로 가지 못해서 글로벌 핸들러가 처리할 수 없음
     * */
    private void setErrorResponse(HttpServletResponse response, String errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(errorCode, message);

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}