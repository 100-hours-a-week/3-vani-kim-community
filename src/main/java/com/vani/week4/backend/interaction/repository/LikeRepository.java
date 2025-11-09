package com.vani.week4.backend.interaction.repository;

import com.vani.week4.backend.interaction.entity.Like;
import com.vani.week4.backend.interaction.entity.UserPostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author vani
 * @since 10/15/25
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, UserPostLikeId> {
    // 좋아요 존재 여부 확인
    boolean existsById(UserPostLikeId id);

    boolean existsByUserIdAndPostId(String userId, String postId);

    int countByUserPostLikeIdPostId(String postId);
}
