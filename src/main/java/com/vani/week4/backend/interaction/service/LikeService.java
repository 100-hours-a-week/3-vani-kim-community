package com.vani.week4.backend.interaction.service;

import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.PostNotFoundException;
import com.vani.week4.backend.interaction.entity.Like;
import com.vani.week4.backend.interaction.entity.UserPostLikeId;
import com.vani.week4.backend.interaction.repository.LikeRepository;
import com.vani.week4.backend.post.entity.Post;
import com.vani.week4.backend.post.repository.PostRepository;
import com.vani.week4.backend.user.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

/**
 * 좋아요 관련 로직을 처리하는 클래스
 * Redis를 사용하여 좋아요 수를 캐싱, 스케줄러로 DB와 동기화
 * @author vani
 * @since 10/15/25
 */
@Service
public class LikeService {
    private static final String LIKE_COUNT_KEY_PREFIX = "post:like:";

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> likesRedisTemplate;

    protected LikeService(
            LikeRepository likeRepository,
            PostRepository postRepository,
            @Qualifier("likesRedisTemplate")RedisTemplate<String, Object> template
        ) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.likesRedisTemplate = template;
    }

    //TODO 레디스와 DB 동기화 오류 문제 있음 캐시가 비었을 경우 다시 DB에서 가져와서 쓰는 로직이 없음
    /**
     * 게시글의 좋아요를 토글
     * 이미 좋아요 했다면 취소, 좋아요하지 않았다면 좋아요합니다.
     * 수는 Redis에 캐싱, 스캐줄러를 통해 DB와 동기화
     * */
    @Transactional
    public void toggleLike(User user, String postId){

        String userId = user.getId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        //이미 좋아요 했다면 삭제, 안했으면 좋아요
        //레디스에 카운트 캐싱
        //키는 텍스트로 읽기 좋게
        if (likeRepository.existsById(new UserPostLikeId(userId, postId))){
            likeRepository.deleteById(new UserPostLikeId(userId, postId));
            likesRedisTemplate.opsForValue().decrement(LIKE_COUNT_KEY_PREFIX + postId);
        } else {
            likeRepository.save(new Like(user, post));
            likesRedisTemplate.opsForValue().increment(LIKE_COUNT_KEY_PREFIX + postId);
        }
    }

    /**
     * Redis에서 좋아요수를 조회하고 없다면 DB에서 로드합니다.
     */
     private Integer getLikeCount(String postId){
         String redisKey = LIKE_COUNT_KEY_PREFIX + postId;
         Object value = likesRedisTemplate.opsForValue().get(redisKey);

         if (value == null){
             //DB에서 조회 후 Redis에 캐싱
             int count = likeRepository.countByUserPostLikeIdPostId(postId);
             likesRedisTemplate.opsForValue().set(redisKey,count);
             return count;
         }

         return Integer.parseInt(value.toString());
     }
}
