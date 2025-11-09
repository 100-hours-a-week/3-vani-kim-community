package com.vani.week4.backend.infra.S3;

import com.vani.week4.backend.global.CurrentUser;
import com.vani.week4.backend.infra.S3.dto.PresignRequest;
import com.vani.week4.backend.infra.S3.dto.PresignResponse;
import com.vani.week4.backend.infra.StorageService;
import com.vani.week4.backend.user.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 이미지 파일 업로드를 위한 presignedUrl 생성 컨트롤러
 * @author vani
 * @since 10/25/25
 */
@RestController
@RequestMapping("api/v1/uploads")
@RequiredArgsConstructor( access = AccessLevel.PROTECTED)
public class UploadController {

    private final StorageService storageService;

    // 인증된 사용자를 위한 업로드
    @PostMapping("/presign")
    public PresignResponse createPresignedUrl (
            @RequestBody PresignRequest request,
            @CurrentUser User user
    ) {
        PresignResponse response = storageService.createPresignedUrl(
                user.getId(),
                request.fileName(),
                request.contentType(),
                request.category(),
                request.fileSizeByte()
        );

        return response;
    }

    // 인증되지 않은 사용자를 위한 업로드
    // TODO 악의적인 업로드 고려 필요
    @PostMapping("/presign/temp")
    public PresignResponse createTempPresignedUrl (
            @RequestBody PresignRequest request
    ) {
        PresignResponse response = storageService.createPresignedUrl(
                UUID.randomUUID().toString(),
                request.fileName(),
                request.contentType(),
                FileCategory.TEMP_PROFILE_IMAGE,
                request.fileSizeByte()
        );

        return response;

    }
}

