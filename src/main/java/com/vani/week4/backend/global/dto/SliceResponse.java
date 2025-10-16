package com.vani.week4.backend.global.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 페이징 응답을 위한 DTO, 게시물들 리스트, 다음 커서, 더 있는지 확인 값
 * @author vani
 * @since 10/14/25
 */
public record SliceResponse<T>(
        List<T> items,
        Cursor nextCursor,
        boolean hasMore
) {
    public record Cursor(
            String id,
            LocalDateTime createdAt
    ) {}
}
