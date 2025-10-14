package com.vani.week4.backend.global.exception;

/**
 * @author vani
 * @since 10/13/25
 */
public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
