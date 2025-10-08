package com.vani.week4.backend.comment.entity;

import com.vani.week4.backend.post.entity.Post;
import com.vani.week4.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 *
 */
@Entity
@Setter
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

    @Column(length = 2000)
    private String content;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CommentStatus commentStatus;
}
