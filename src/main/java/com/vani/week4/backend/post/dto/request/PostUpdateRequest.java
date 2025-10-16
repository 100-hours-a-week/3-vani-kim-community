package com.vani.week4.backend.post.dto.request;

import jakarta.validation.constraints.Size;

/**
 * @author vani
 * @since 10/15/25
 */
public record PostUpdateRequest(
        @Size(min = 1, max = 255, message = "제목은 1-255자 사이여야 합니다")
        String title,

        @Size(max = 10000, message = "내용은 최대 10000자입니다.")
        String content,

        String postImageKey
) {}
