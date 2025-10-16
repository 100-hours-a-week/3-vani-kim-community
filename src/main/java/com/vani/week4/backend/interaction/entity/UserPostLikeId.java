package com.vani.week4.backend.interaction.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// JPA 표준 스펙 : Embeddable은  Serializable 인터페이스 필수
public class UserPostLikeId implements Serializable {
    private String userId;
    private String postId;

    // 동일성 판단 기준 : Lombok의 @Data로 대체 가능
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPostLikeId that = (UserPostLikeId) o;
        return userId.equals(that.userId) && postId.equals(that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }
}