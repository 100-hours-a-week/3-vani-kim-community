package com.vani.week4.backend.global.config;

import com.vani.week4.backend.global.CurrentsUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 스프링 MVC 레벨의 설정을 하는 클래스
 * @author vani
 * @since 10/28/25
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentsUserArgumentResolver currentsUserArgumentResolver;

    // Argument Resolver에 @CurrentUser 추가
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentsUserArgumentResolver);
    }

    // 스프링 mvc 수준에서 동작하기에 필터에서 반환되는 오류에 대해 CORS설정이 안된다.
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 1. 모든 API 엔드포인트 ("/**")
//                .allowedOrigins("http://localhost:3000") // 2. 프론트엔드 출처(:3000) 허용
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true); // 3. ★★★ 쿠키를 주고받기 위한 필수 설정
//    }


}
