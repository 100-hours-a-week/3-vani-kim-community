package com.vani.week4.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 레디스 설정 클래스
 * 토큰과 좋아요를 위한 각각의 템플릿 생성
 * @author vani
 * @since 10/14/25
 */

//TODO 레디스 활용 확장 필요 캐싱 등, 템플릿 유연하게
@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * 리프레시 토큰을 위한 레디스 연결 팩토리
     * 레디스 DB 0번을 사용
     */

    @Bean(name = "redisTokenConnectionFactory")
    @Primary    // 기본 팩터리
    public RedisConnectionFactory redisTokenConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(0);
        return new LettuceConnectionFactory(config);
    }

    // 좋아요를 위한 레디스 연결 팩토리
    @Bean(name = "redisLikesConnectionFactory")
    public RedisConnectionFactory redisLikesConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(1);
        return new LettuceConnectionFactory(config);
    }

    // 기본 레디스 템플릿(토큰용)
    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisTokenConnectionFactory());

        StringRedisSerializer serializer = new StringRedisSerializer();
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    //좋아요를 위한 레디스 탬플릿
    @Bean(name = "likesRedisTemplate")
    public RedisTemplate<String, Object> likesRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisLikesConnectionFactory());

        // Key 는 문자열 Value는 숫자로 처리
        StringRedisSerializer serializer = new StringRedisSerializer();
        GenericToStringSerializer<Object> genericToStringSerializer = new GenericToStringSerializer<>(Object.class);
        template.setKeySerializer(serializer);
        template.setValueSerializer(genericToStringSerializer);

        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(genericToStringSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
