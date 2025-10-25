package com.vani.week4.backend.infra.S3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vani
 * @since 10/24/25
 */
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    //클라언트에게 파일명, Content-Type을 받아 PreSigned로
    @PostMapping("/presigned-url/upload")
    public ResponseEntity<String> getUploadPreSignedUrl(
            @RequestParam String objectKey,
            @RequestParam String contentType) {
        String presignedUrl = s3Service.generatePresignedUploadUrl(objectKey, contentType);

        return ResponseEntity.ok(presignedUrl);
    }
}
