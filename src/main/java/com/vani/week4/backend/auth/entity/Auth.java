package com.vani.week4.backend.auth.entity;

import com.vani.week4.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author vani
 * @since 10/8/25
 */
// TODO email필드를 제거해야함. 현재는 로그인시 Auth탐색용으로 사용... identifier등으로 대체
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_auths")
public class Auth {
    @Id
    @Column(length = 26)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 300)
    private String email;

    @Column(length = 10)
    private ProviderType provider;

    private String passwordHash;

    //정적 팩토리, 상태변경, 연관관계 편의 메서드, 내부계산, 간단 검증
    @Builder
    private Auth(String id, User user, String email, ProviderType provider, String passwordHash){
        this.id = id;
        this.user = user;
        this.email = email;
        this.provider = provider;
        this.passwordHash = passwordHash;
    }

    public static Auth ceateAuth(User user, String id, String email, ProviderType provider, String passwordHash){
        return Auth.builder()
                .id(id)
                .user(user)
                .email(email)
                .provider(provider)
                .passwordHash(passwordHash)
                .build();
    }

}
