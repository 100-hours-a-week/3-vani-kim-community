package com.vani.week4.backend.global.exception;

/**
 * @author vani
 * @since 10/13/25
 */
public class InvalidCommentException extends RuntimeException{
    public InvalidCommentException(String message) {
        super(message);
    }
}
