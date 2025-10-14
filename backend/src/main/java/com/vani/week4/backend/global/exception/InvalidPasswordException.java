package com.vani.week4.backend.global.exception;

/**
 * @author vani
 * @since 10/13/25
 */
public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(String message) {
        super(message);
    }
}
