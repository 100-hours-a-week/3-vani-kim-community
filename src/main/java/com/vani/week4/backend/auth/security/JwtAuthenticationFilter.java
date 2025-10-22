package com.vani.week4.backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.UserNotFoundException;
import com.vani.week4.backend.user.entity.User;
import com.vani.week4.backend.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * JWT사용을 위한 커스텀 필터, 토큰 검증
 * @author vani
 * @since 10/14/25
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    /**
     * 필터 추가 메서드
     * */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        //TODO 개발용... 이거 지워야함...
        // (★ 추가 ★) OPTIONS 요청은 JWT 검증 없이 즉시 통과 (CORS Preflight 처리)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            // filterChain.doFilter(request, response); // OPTIONS는 여기서 끝내야 함
            return; // 필터 체인 진행 중단하고 즉시 응답 반환
        }
        String token = resolveToken(request);
        try{
            //TODO 블랙리스트 추가시 변경 필수, 권한 설정 필요
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserIdFromToken(token);

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

                UserPrincipal userPrincipal = new UserPrincipal(user);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,       // JWT에선 비밀번호 불필요
                        userPrincipal.getAuthorities() // 권한(Role)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // 토큰 만료시
            setErrorResponse(response, "T002", "Access Token Expired");
            return;
        } catch (SignatureException | MalformedJwtException e) {
            //토큰 위조/형식 오류 예외
            setErrorResponse(response, "T002", "Invalid Token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);        //"Bearer " 제거
        }
        return null;
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
