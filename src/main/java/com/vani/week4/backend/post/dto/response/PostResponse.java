package com.vani.week4.backend.post.dto.response;

import java.time.LocalDateTime;

/**
 * @author vani
 * @since 10/15/25
 */
public record PostResponse(
        String postId,
        String title,
        LocalDateTime createdAt,
        ContentDetail contentDetail,
        Author author
) {
    public record ContentDetail(
            String content,
            String postImageKey
    ) {}
    //TODO 유저 프로필 해결
    public record Author (
            String nickname
    ) {}
}