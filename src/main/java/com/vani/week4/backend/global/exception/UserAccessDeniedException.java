package com.vani.week4.backend.global.exception;

/**
 * @author vani
 * @since 10/13/25
 */
public class UserAccessDeniedException extends RuntimeException{
    public UserAccessDeniedException(String message) {
        super(message);
    }
}
