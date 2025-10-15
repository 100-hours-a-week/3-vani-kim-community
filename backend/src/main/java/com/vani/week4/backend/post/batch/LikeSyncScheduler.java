package com.vani.week4.backend.post.batch;

import com.vani.week4.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author vani
 * @since 10/15/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LikeSyncScheduler {
    private final RedisTemplate<String, Object> redisTemplateForCount;
    private final PostRepository postRepository;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void synLikeCount() {
        log.info("좋아요 수 동기화 시작");

        //5분마다 동기화
        Set<String> keys = redisTemplateForCount.keys("post:like:*");

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
                Object value = redisTemplateForCount.opsForValue().get(key);

                if (value == null) {
                    continue;
                }

                Integer likeCount = value instanceof Long
                        ? ((Long) value).intValue()
                        : (Integer) value;

                //DB 업데이트
                postRepository.findById(postId).ifPresent(post -> {
                    post.updateLikeCount(likeCount);
                    log.debug("Post {} 좋아요 수 업데이트: {}", postId, likeCount);

                });

                syncCount++;
            } catch (Exception e) {
                log.error("좋아요 수 동기화 실패 - key:{}, error: {}", key, e.getMessage());
            }
        }
        log.info("좋아요 수 동기화 완료 : 처리 수: {}", syncCount);
    }
}
