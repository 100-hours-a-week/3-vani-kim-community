package com.vani.week4.backend.infra.S3;

/**
 * @author vani
 * @since 10/26/25
 */
public record PresignTempRequestDto (
            String filename,
            String contentType,
            Long fileSize
) {
}
