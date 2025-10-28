package com.vani.week4.backend.global.exception;

import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.infra.S3.S3Service;
import lombok.Getter;

/**
 * @author vani
 * @since 10/25/25
 */
@Getter
public class InvalidContentTypeException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidContentTypeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
