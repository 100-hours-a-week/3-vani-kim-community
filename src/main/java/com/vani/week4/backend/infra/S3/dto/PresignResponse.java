package com.vani.week4.backend.infra.S3.dto;

/**
 * @author vani
 * @since 10/25/25
 */
public record PresignResponse(
        String presignedUrl,
        String objectKey,
        String contentType
) {
}
