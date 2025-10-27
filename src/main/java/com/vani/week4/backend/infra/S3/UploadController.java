package com.vani.week4.backend.infra.S3;

import com.vani.week4.backend.global.CurrentUser;
import com.vani.week4.backend.infra.StorageService;
import com.vani.week4.backend.user.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author vani
 * @since 10/25/25
 */
@RestController
@RequestMapping("api/v1/uploads")
@RequiredArgsConstructor( access = AccessLevel.PROTECTED)
public class UploadController {

    private final StorageService storageService;

    @PostMapping("/presign")
    public PresignResponseDto createPresignedUrl (
            @RequestBody PresignRequestDto request,
            @CurrentUser User user
    ) {
        PresignResponse response = storageService.createPresignedUrl(
                user.getId(),
                request.filename(),
                request.contentType(),
                request.category(),
                request.fileSize()
        );

        return new PresignResponseDto(
                response.uploadUrl(),
                response.fileUrl(),
                response.contentType()
        );
    }

    @PostMapping("/presign/temp")
    public PresignResponseDto createTempPresignedUrl (
            @RequestBody PresignRequestDto request
    ) {
        PresignResponse response = storageService.createPresignedUrl(
                UUID.randomUUID().toString(),
                request.filename(),
                request.contentType(),
                FileCategory.TEMP_PROFILE_IMAGE,
                request.fileSize()
        );

        return new PresignResponseDto(
                response.uploadUrl(),
                response.fileUrl(),
                response.contentType()
        );

    }
}

