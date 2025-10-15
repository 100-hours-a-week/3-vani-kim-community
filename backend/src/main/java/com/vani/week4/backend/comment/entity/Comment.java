package com.vani.week4.backend.comment.entity;

import com.vani.week4.backend.post.entity.Post;
import com.vani.week4.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @Column(length = 26)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(length = 26)
    private String parentId;

    private Integer depth;

    @Column(length = 26)
    private String commentGroup;

    @Column(length = 1000)
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CommentStatus commentStatus;

    @Builder
    private Comment(String id, User user, Post post, String parentId,
                    Integer depth, String commentGroup, String content) {
        this.id = id;
        this.user = user;
        this.post = post;
        this.parentId = parentId;
        this.depth = depth;
        this.commentGroup = commentGroup;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.commentStatus = CommentStatus.ACTIVE;
    }

    // 루트 댓글의 commentGroup 업데이트
    public void updateCommentGroup(String commentGroup) {
        this.commentGroup = commentGroup;
    }

    // 수정
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // Soft delete
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.commentStatus = CommentStatus.DELETED;
    }

}
