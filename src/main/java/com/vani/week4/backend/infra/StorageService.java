package com.vani.week4.backend.infra;

import com.vani.week4.backend.infra.S3.FileCategory;
import com.vani.week4.backend.infra.S3.PresignResponse;

//TODO 이미지 처리와 회원가입, 게시글 생성등의 API 분리하기
/**
 * 스토리지(S3, 로컬 파일 시스템 등)에 파일 업로드 관련 기능을 정의하는 서비스 인터페이스
 * 구현체는 presigned URL을 생성하거나, 파일 업로드 정책을 결정함
 * @author vani
 * @since 10/25/25
 */

public interface StorageService {
    /**
     * 지정된 사용자 ID와 파일 정보로 presigned 업로드 URL생성
     *
     * @param userId 업로드를 요청한 사용자
     * @param filename 원본 파일 이름(확장자 포함)
     * @param contentType MIME 타입(image/png,jpeg,jpg,webp)허용 향후 확장 고려
     * @param category 파일의 종류(Profile, Post 등)
     * @param fileSize 전송된 파일의 크기
     * @return 업로드용 presigned URL과 최종 파일 URL 정보를 담은 객체
     * @throws InvalidContentTypeException 지원되지 않는 MINE 타입일 경우
     * */
    PresignResponse createPresignedUrl(String userId, String filename, String contentType, FileCategory category, long fileSize);
}

