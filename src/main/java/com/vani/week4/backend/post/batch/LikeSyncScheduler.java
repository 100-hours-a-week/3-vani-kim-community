package com.vani.week4.backend.post.batch;

import com.vani.week4.backend.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Redis 에 저장된 좋아요 수를 5분마다 DB와 동기화하기 위한 스케쥴러
 * likesRedisTemplate를 이용
 * @author vani
 * @since 10/15/25
 */

@Component
@Slf4j
public class LikeSyncScheduler {
    private final RedisTemplate<String, Object> likesRedisTemplate;
    private final PostRepository postRepository;

    public LikeSyncScheduler(
            @Qualifier("likesRedisTemplate") RedisTemplate<String, Object> template,
            PostRepository postRepository) {
        this.likesRedisTemplate = template;
        this.postRepository = postRepository;
    }

    /**
     * Redis의 좋아요 수를 DB와 동기화
     * 5분마다 실행되며, "post:like:*" 패턴의 모든 키를 조회하여 DB를 업데이트합니다.
     */
    @Scheduled(cron = "0 */5 * * * *") //5분마다 동기화
    @Transactional
    public void synLikeCount() {
        log.info("좋아요 수 동기화 시작");
        Set<String> keys = likesRedisTemplate.keys("post:like:*");

        if (keys == null || keys.isEmpty()) {
            log.info("동기화할 데이터가 없습니다.");
            return;
        }

        int syncCount = 0;

        for (String key : keys) {
            try {
                // 키에서 postId추출
                String postId = key.substring("post:like:".length());

                // Redis에서 좋아요 수 조회
                Object value = likesRedisTemplate.opsForValue().get(key);

                if (value == null) {
                    continue;
                }

                //string to integer
                Integer likeCount = Integer.parseInt(value.toString());

                //DB 업데이트
                postRepository.findById(postId).ifPresent(post -> {
                    post.updateLikeCount(likeCount);
                });

                syncCount++;
            } catch (NumberFormatException e) {
                log.error("Redis 값을 숫자로 변환 실패 - key:{}", key, e);
            } catch (Exception e) {
                log.error("좋아요 수 동기화 실패 - key:{}, error: {}", key, e.getMessage());
            }
        }
        log.info("좋아요 수 동기화 완료 : 처리 수: {}", syncCount);
    }
}
