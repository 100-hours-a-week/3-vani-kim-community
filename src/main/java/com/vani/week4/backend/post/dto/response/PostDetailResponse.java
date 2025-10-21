package com.vani.week4.backend.post.dto.response;

import java.time.LocalDateTime;

/**
 * @author vani
 * @since 10/14/25
 */
public record PostDetailResponse (
        String postId,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ContentDetail contentDetail,
        Author author,
        Stats stats
) {
    public record ContentDetail(
            String content,
            String postImageKey
    ) {}
    public record Author (
            String name
    ) {}
    public record Stats(
            long likeCount,
            long commentCount,
            long viewCount
    ) {}
}