package com.vani.week4.backend.infra.S3;

/**
 * 용도에 따른 파일 구분,
 * 저장 경로 / 접근 정책을 다르게 하기 위한 구분자
 * @author vani
 * @since 10/25/25
 */
public enum FileCategory {
    PROFILE_IMAGE,
    POST_IMAGE,
    TEMP_PROFILE_IMAGE,
    OTHER
}
