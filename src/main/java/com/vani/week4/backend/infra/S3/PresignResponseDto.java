package com.vani.week4.backend.infra.S3;

/**
 * @author vani
 * @since 10/25/25
 */
public record PresignResponseDto(
        String uploadUrl,
        String fileUrl,
        String ContentType

) {
}
