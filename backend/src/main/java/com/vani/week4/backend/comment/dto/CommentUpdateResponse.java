package com.vani.week4.backend.comment.dto;

import java.time.LocalDateTime;

/**
 * @author vani
 * @since 10/15/25
 */
public record CommentUpdateResponse(
        String id,
        String content,
        LocalDateTime updatedAt
) { }