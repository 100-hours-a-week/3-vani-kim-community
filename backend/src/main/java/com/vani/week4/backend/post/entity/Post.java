package com.vani.week4.backend.post.entity;

import com.vani.week4.backend.comment.entity.Comment;
import com.vani.week4.backend.interaction.entity.Like;
import com.vani.week4.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
public class Post {
    @Id
    @Column(length = 26)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id" )
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    @Column(length = 100)
    private String title;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    private Integer view_count;
    private Integer comment_count;
    private Integer like_count;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PostStatus postStatus;

}
