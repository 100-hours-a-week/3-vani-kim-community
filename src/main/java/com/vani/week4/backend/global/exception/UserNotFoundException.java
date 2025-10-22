package com.vani.week4.backend.global.exception;

import com.vani.week4.backend.global.ErrorCode;
import lombok.Getter;

/**
 * @author vani
 * @since 10/13/25
 */
@Getter
public class UserNotFoundException extends RuntimeException{

    private final ErrorCode errorCode;

    public UserNotFoundException(ErrorCode errorCode) {

        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
