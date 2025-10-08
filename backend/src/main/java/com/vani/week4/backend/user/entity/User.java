package com.vani.week4.backend.user.entity;

import com.vani.week4.backend.interaction.entity.Like;
import com.vani.week4.backend.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @Column(length = 26)
    private String id;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    @Column(length = 300, nullable = false, unique = true)
    private String email;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    @Enumerated(EnumType.STRING)
    @Column(length = 20) //DB나 서버 둘중 하나만봐도 어떤 상태인지 알 수 있도록 varchar
    private UserStatus userStatus;

    protected User(){}

    //필요시 생성자 구현
}
