package com.vani.week4.backend.infra.S3;

import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.FileSizeExceedException;
import com.vani.week4.backend.global.exception.InvalidContentTypeException;
import com.vani.week4.backend.infra.S3.dto.PresignResponse;
import com.vani.week4.backend.infra.StorageService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;


import java.time.Duration;
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

    private final S3Presigner s3Presigner;
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3 파일 업로드용 Presigned PUT URL 생성
     * @param userId s3에 저장할 파일 경로 및 이름
     * @param Filename 전달 받은 파일의 이름, 확장자
     * @param contentType 파일의 Content-Type
     * @return Presigned URL
     */
    @Override
    public PresignResponse createPresignedUrl(
            String userId,
            String Filename,
            String contentType,
            FileCategory category,
            long fileSizeByte
    ) {
        //1. MIME 타입 화이트 리스트와 파일 크기 확인
        if(!isAllowedContentType(contentType)) {
            throw new InvalidContentTypeException(ErrorCode.INVALID_INPUT);
        }

        if(fileSizeByte > MAX_FILE_SIZE) {
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

        return new PresignResponse(
                presigned.url().toString(),
                objectKey,
                contentType
        );
    }

    /**
     * S3 파일 조회/디운로드용 presigned GET URL 생성하는 메서드
     * @param objectKey 가져올 파일의 오브젝트 키
     * @return presignedGetURL
     * */
    public String createPresignedGetUrl(String objectKey) {

        // 1. S3에서 어떤 객체를 가져올지 정보 정의
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        //2. Presigned GET 요청 생성(10 분)
        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(r -> r
                .signatureDuration(Duration.ofHours(10))
                .getObjectRequest(getReq)
        );

        return presigned.url().toString();
    }

    /**
     * 전달 받은 정보가 허용된 이미지 타입을 체크하는 메서드
     * png,jpeg,jpg,webp 만허용(향후 확장)
     */
    private boolean isAllowedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpeg")
                || contentType.equals("image/jpg")
                || contentType.equals("image/webp");
    }

    /**
     * 확인한 contentType을 실제 파일 확장자로 변환해주는 메서드
     * */
    private String guessExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/webp" -> "webp";
            default -> "bin";
        };
    }

    /**
     * ObjectKey를 생성하는 메서드
     * 회원가입 시 업로드 처리는 temp폴더로 한 후 이동
     * @param userId 요청한 사용자
     * @param category 파일의 종류(프로필 이미지, 게시글 이미지)
     * @param extension 파일의 확장자
     * @return objectKey
     * */
    //TODO S3 GC 설정 필요
    private String buildObjectKey(String userId, FileCategory category, String extension) {
        if (category == FileCategory.TEMP_PROFILE_IMAGE){
            String folder = "temp";
            return folder + "/" + userId + "." + extension;
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
