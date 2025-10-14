package com.vani.week4.backend.user.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 유저의 정보를 담는 클래스, 향후 확장
 * @author vani
 * @since 10/10/25
 *
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @Column(name = "user_id")
    private String id;  // @Id에 맵핑관계 바로 설정 x

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId  // User의 ID를 이 엔티티의 ID로 사용
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 300, nullable = false, unique = true)
    private String email;

    private UserProfile(User user, String email) {
        this.user = user;
        this.email = email;
    }
    public static UserProfile createUserprofile(User user, String email) {
        return new UserProfile(user, email);
    }

    public void updateEmail(String email) {
        this.email = email;
    }
}
