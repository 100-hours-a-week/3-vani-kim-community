package com.vani.week4.backend.comment.repository;

import com.vani.week4.backend.comment.entity.Comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vani
 * @since 10/15/25
 */
public interface CommentRepository extends JpaRepository<Comment,String> {
    // 우선 생성+아이디로 내림차순
    // 루트댓글만 가져옴 : parentId IS NULL
    // 유저의 정보도 필요하기 때문에 N+1 방지 위해서 패치 조인
    @Query( "SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.post.id = :postId " +
            "AND c.parentId IS NULL " +
            "AND (:cursorCreatedAt IS NULL OR " +
            "c.createdAt < :cursorCreatedAt OR " +
            "(c.createdAt = :cursorCreatedAt AND c.id < :cursorId)) " +
            "ORDER BY c.createdAt DESC, c.id DESC")
    Slice<Comment> findTopLevelComments(
            @Param("postId") String postId,
            @Param("cursorCreatedAt")LocalDateTime cursorCreatedAt,
            @Param("cursorId") String CursorId,
            Pageable pageable
            );

    // 동일 CommentGroup의 댓글 모음 가져오기
    // commentGroup이 루트댓글들의 아이디이므로 루트댓글을 제외한 글들을 가져옴
    // 최근에 달릴수록 최초 댓글에서 멀리 있으니까 오름차순(오래된순) 정렬
    @Query( "SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.commentGroup = :commentGroup " +
            "AND c.id != :commentGroup " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByCommentGroup(@Param("commentGroup") String commentGroup);
}
