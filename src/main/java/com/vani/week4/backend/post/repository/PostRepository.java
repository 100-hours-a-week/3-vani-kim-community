package com.vani.week4.backend.post.repository;

import com.vani.week4.backend.post.entity.Post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author vani
 * @since 10/14/25
 */
public interface PostRepository extends JpaRepository<Post, String> {

    // 커서 기반 페이지네이션, 첫 조회를 위한 조건
    @Query("SELECT p FROM Post p WHERE " +
            "(:cursorCreatedAt IS NULL OR " +
            "p.createdAt < :cursorCreatedAt OR " +
            "(p.createdAt = :cursorCreatedAt AND p.id < :cursorId)) " +
            "ORDER BY p.createdAt DESC, p.id DESC")
    Slice<Post> findByCursor(
            @Param("cursorId") String cursorId,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            Pageable pageable
    );

    // 게시글의 정보를 위한 쿼리(author, content포함)
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.postContent " +
            "LEFT JOIN FETCH p.user " +
            "WHERE p.id = :postId")
    Optional<Post> findByIdWithContent(@Param("postId") String postId);

}
