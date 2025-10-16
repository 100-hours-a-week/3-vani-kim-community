package com.vani.week4.backend.interaction.dto;

/**
 * @author vani
 * @since 10/15/25
 */
public record LikeResponse(
        String postId,
        long likeCount
) {
}
