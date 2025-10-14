package com.vani.week4.backend.global;

import com.vani.week4.backend.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * PasswordEncoder 빈 등록, 필터 사용 설정
 * @author vani
 * @since 10/13/25
 */
@Configuration
@EnableWebSecurity //스프링 시큐리티 커스터마이징할 경우
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 필터 체인에서 언제 어떤 필터 쓸지 설정하는 메서드
     * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //CSRF  비활성화
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.disable())

                // 세션 사용 x
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //url별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/users").permitAll()
                        .requestMatchers("/auth/tokens").permitAll()
                        .requestMatchers("/auth/refresh").permitAll()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
