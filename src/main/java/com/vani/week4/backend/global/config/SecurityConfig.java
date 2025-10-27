package com.vani.week4.backend.global.config;
import com.vani.week4.backend.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest; // 👈 1. 임포트
import java.util.List;

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

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션 사용 x
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //url별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // 3. 에러 페이지 허용 (필수)
                        .requestMatchers("/error").permitAll()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/users", "/auth/tokens", "/auth/refresh", "/auth/nickname","/auth/email").permitAll()
                        .requestMatchers("/api/v1/uploads/presign/temp").permitAll()
                        .requestMatchers("/terms-of-service").permitAll()
                        .requestMatchers("/privacy-policy").permitAll()
                        .requestMatchers(
                                "/",                          // 루트 경로 허용
                                "/swagger-ui.html",           // Swagger UI 페이지
                                "/swagger-ui/**",             // Swagger UI 리소스 (js, css 등)
                                "/v3/api-docs/**"             // API 명세서 JSON
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/index")
                        .permitAll()
                )

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5500", "http://127.0.0.1:5500", "http://127.0.0.1:3000","http://localhost:3000")); // 허용할 출처
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 메서드
        configuration.setAllowedHeaders(List.of("*")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정 적용
        return source;
    }

}
