package com.vani.week4.backend.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author vani
 * @since 10/22/25
 */

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // === 4xx Client Error === //

    //400 Bad Request
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E400-1", "입력값이 유효하지 않습니다."),

    //401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED , "E401-1", "인증되지 않은 사용자입니다."),

    //403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN , "E403-1", "접근 권한이 없습니다."),

    //404 NotFound
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND , "E404-1", "해당 리소스를 찾을 수 없습니다."),

    // 409 Conflict (요청하신 것)
    // - 리소스 상태 충돌 (예: 이메일 중복)
    RESOURCE_CONFLICT(HttpStatus.CONFLICT, "E409-1", "데이터 충돌이 발생했습니다."),

    // 429 Too Many Requests
    // - 단시간에 너무 많은 요청을 보냄 (Rate Limiting)
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "E429-1", "요청 횟수를 초과했습니다. 잠시 후 다시 시도해주세요."),

    // === 5xx Server Error === //

    // 500 Internal Server Error
    // - 서버 내부 로직 오류 (NullPointerException 등)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500-1", "서버가 아파요."),

    // 503 Service Unavailable
    // - 서버가 일시적으로 사용 불가능 (과부하, 점검 등)
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "E503-1", "서비스를 일시적으로 이용할 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

}
