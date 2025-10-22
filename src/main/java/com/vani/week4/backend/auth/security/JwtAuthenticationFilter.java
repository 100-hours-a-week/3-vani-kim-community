package com.vani.week4.backend.auth.security;

import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.UserNotFoundException;
import com.vani.week4.backend.user.entity.User;
import com.vani.week4.backend.user.repository.UserRepository;
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
    /**
     * 필터 추가 메서드
     * */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // (★ 추가 ★) OPTIONS 요청은 JWT 검증 없이 즉시 통과 (CORS Preflight 처리)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            // filterChain.doFilter(request, response); // OPTIONS는 여기서 끝내야 함
            return; // 필터 체인 진행 중단하고 즉시 응답 반환
        }
        String token = resolveToken(request);

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

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);        //"Bearer " 제거
        }
        return null;
    }

}
