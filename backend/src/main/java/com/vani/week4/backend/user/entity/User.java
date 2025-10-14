package com.vani.week4.backend.user.entity;

import com.vani.week4.backend.interaction.entity.Like;
import com.vani.week4.backend.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 유저클래스, 자주 탐색되는 정보 필드
 * @author vani
 * @since 10/10/25
 *
 */
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "users")
public class User {
    @Id
    @Column(length = 26)
    private String id;

    @Column(length = 10, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 20) //DB나 서버 둘중 하나만봐도 어떤 상태인지 알 수 있도록 varchar
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 10) //DB나 서버 둘중 하나만봐도 어떤 상태인지 알 수 있도록 varchar
    private UserRole userRole;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    protected User(){}

    public static User createUser(String id, String nickname, UserStatus userStatus){
        return User.builder()
                .id(id)
                .nickname(nickname)
                .userStatus(userStatus)
                .userRole(UserRole.USER)
                .created_at(LocalDateTime.now())
                .build();
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updateUserStatus(UserStatus userStatus){
        this.userStatus = userStatus;
    }

    public boolean isActive() {
        return this.userStatus == UserStatus.ACTIVE;
    }

}
