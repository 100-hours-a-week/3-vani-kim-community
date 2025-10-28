package com.vani.week4.backend.interaction.service;

import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.PostNotFoundException;
import com.vani.week4.backend.interaction.entity.Like;
import com.vani.week4.backend.interaction.entity.UserPostLikeId;
import com.vani.week4.backend.interaction.repository.LikeRepository;
import com.vani.week4.backend.post.entity.Post;
import com.vani.week4.backend.post.repository.PostRepository;
import com.vani.week4.backend.user.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author vani
 * @since 10/15/25
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplateForCount;
    //TODO 레디스와 DB 동기화 오류 문제 있음 캐시가 비었을 경우 다시 DB에서 가져와서 쓰는 로직이 없음
    @Transactional
    public void toggleLike(User user, String postId){

        String userId = user.getId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        //이미 좋아요 했다면 삭제, 안했으면 좋아요
        //레디스에 카운트 캐싱
        if (likeRepository.existsById(new UserPostLikeId(userId, postId))){
            likeRepository.deleteById(new UserPostLikeId(userId, postId));
            redisTemplateForCount.opsForValue().decrement("post:like:" + postId);
        } else {
            likeRepository.save(new Like(user, post));
            redisTemplateForCount.opsForValue().increment("post:like:" + postId);
        }
    }
}
