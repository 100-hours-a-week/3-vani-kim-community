package com.vani.week4.backend.auth.config;

import com.vani.week4.backend.auth.security.SessionAuthFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 웹 요청 필터를 애플리케이션으로 등록하는 스프링 설정 클래스
 * @author vani
 * @since 10/28/25
 */
@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {
    private final SessionAuthFilter sessionAuthFilter;

    @Bean
    public FilterRegistrationBean<Filter> sessionFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(sessionAuthFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    // 필터에서 쓸수 있게 직접 bean 등록
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        CorsConfiguration config = new CorsConfiguration();

        //쿠키 교환 가능하게
        config.setAllowCredentials(true);
        //프론트엔트 출처 허용하기
        config.addAllowedOrigin("http://localhost:3000");
        //허용할 헤더
        config.addAllowedHeader("*");
        //허용할 메서드
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>(new CorsFilter(source));

        // CorsFilter가 가장 먼저 실행 되도록
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterBean;
    }
}
