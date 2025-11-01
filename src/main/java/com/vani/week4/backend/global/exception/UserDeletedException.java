package com.vani.week4.backend.global.exception;

import com.vani.week4.backend.global.ErrorCode;
import lombok.Getter;

/**
 * @author vani
 * @since 10/13/25
 */
@Getter
public class UserDeletedException extends RuntimeException{

    private final ErrorCode errorCode;

    public UserDeletedException(ErrorCode errorCode) {

        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
