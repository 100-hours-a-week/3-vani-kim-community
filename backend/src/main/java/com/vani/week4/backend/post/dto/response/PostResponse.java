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
    public record Author (
            String name,
            String email
    ) {}
}