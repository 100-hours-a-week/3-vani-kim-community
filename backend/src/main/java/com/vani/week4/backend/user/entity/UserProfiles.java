package com.vani.week4.backend.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;;

/**
 * @author vani
 * @since 10/10/25
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDisplay {
    @Id
    @Column(length = 26)
    private String userId;

    @Column(length = 10)
    private String nickname;

    private String profileImageUrl;

}
