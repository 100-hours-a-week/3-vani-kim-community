package com.vani.week4.backend.infra.S3.dto;

import com.vani.week4.backend.infra.S3.FileCategory;
import jakarta.validation.constraints.NotBlank;

/**
 * @author vani
 * @since 10/25/25
 */
public record PresignRequest(
        @NotBlank(message = "파일명은 필수입니다")
        String filename,
        @NotBlank(message = "콘텐츠 타입은 필수입니다")
        String contentType,
        @NotBlank(message = "파일 사이즈는 필수입니다")
        Long fileSizeByte,
        @NotBlank(message = "카테고리는 필수입니다")
        FileCategory category
) {
}
