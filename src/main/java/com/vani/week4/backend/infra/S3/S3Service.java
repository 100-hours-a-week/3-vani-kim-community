package com.vani.week4.backend.infra.S3;

import com.vani.week4.backend.auth.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;



import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author vani
 * @since 10/16/25
 * S3 로직을 담는 클래스
 */
//TODO key 구성 결정
@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("{cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3 파일 업로드용 Presigned PUT URL 생성
     * @param objectKey s3에 저장할 파일 경로 및 이름
     * @param contentType 파일의 Content-Type
     * @return Presigned URL
     */
    public String generatePresignedUploadUrl(String objectKey, String contentType) {

        //S3에 PUT 요청할 객체 정보 정의
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                //Express에서 업로드시 Content-Type해더 필수
                .contentType(contentType)
                .build();

        //Presigned 요청 생성 (서명기간 10분)
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r -> r
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofHours(10))
        );

        return presignedRequest.url().toString();
    }
}
