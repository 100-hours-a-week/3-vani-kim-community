package com.vani.week4.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Optional;

/**
 * @author vani
 * @since 10/15/25
 */
public record CommentCreateRequest(
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(min = 1, max = 1000, message = "댓글은 1-1000자 시이여야합니다.")
        String content,

        Optional<String> parentId
) {
}
