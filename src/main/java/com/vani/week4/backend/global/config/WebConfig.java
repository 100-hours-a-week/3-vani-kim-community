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

}
