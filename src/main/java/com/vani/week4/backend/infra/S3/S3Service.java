package com.vani.week4.backend.infra.S3;

import com.vani.week4.backend.auth.dto.response.LoginResponse;
import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.FileSizeExceedException;
import com.vani.week4.backend.global.exception.InvalidContentTypeException;
import com.vani.week4.backend.infra.StorageService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;


import java.net.URL;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author vani
 * @since 10/16/25
 * S3 로직을 담는 클래스
 */
//TODO key 구성 결정
@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * S3 파일 업로드용 Presigned PUT URL 생성
     * @param userId s3에 저장할 파일 경로 및 이름
     * @param Filename
     * @param contentType 파일의 Content-Type
     * @return Presigned URL
     */
    @Override
    public PresignResponse createPresignedUrl(
            String userId,
            String Filename,
            String contentType,
            FileCategory category,
            long fileSize
    ) {
        //1. MIME 타입 화이트 리스트와 파일 크기 확인
        if(!isAllowedContentType(contentType)) {
            throw new InvalidContentTypeException(ErrorCode.INVALID_INPUT);
        }

        if(fileSize > MAX_FILE_SIZE) {
            throw new FileSizeExceedException(ErrorCode.INVALID_INPUT);
        }

        //2. 업로드 경로
        String extension = guessExtension(contentType);
        String objectKey = buildObjectKey(userId, category, extension);

        //3. S3에 어떤 객체를 올릴지 정보 정의
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        //4. Presigned 요청 생성 (서명기간 10분)
        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofHours(10))
                .putObjectRequest(putReq)
        );

        //5. 업로드 후 최종 접근 URL
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        URL url = s3Client.utilities().getUrl(getUrlRequest);
        String fileUrl = url.toString();
        return new PresignResponse(
                presigned.url().toString(),
                fileUrl,
                contentType
        );
    }


    private boolean isAllowedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpeg")
                || contentType.equals("image/jpg")
                || contentType.equals("image/webp");
    }

    private String guessExtension(String contentType) {
        if (contentType.equals("image/png")) return "png";
        if (contentType.equals("image/jpeg")|| contentType.equals("image/jpg")) return "jpg";
        if (contentType.equals("image/webp")) return "webp";
        return "bin";
    }
    //TODO S3 GC 설정 필요
    private String buildObjectKey(String userId, FileCategory category, String extension) {
        if (category == FileCategory.TEMP_PROFILE_IMAGE){
            String folder = "temp";
            return folder + "/" + userId+ "." + extension;
        } else {
            String folder = switch(category) {
                case PROFILE_IMAGE -> "profile";
                case POST_IMAGE -> "post";

                default -> "misc";
            };
            return "users/" + userId + "/" + folder + "/" + UUID.randomUUID() + "." + extension;
        }
    }
}
