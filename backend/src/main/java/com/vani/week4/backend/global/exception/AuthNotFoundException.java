package com.vani.week4.backend.global.exception;

/**
 * @author vani
 * @since 10/13/25
 */
public class AuthNotFoundException extends RuntimeException{
    public AuthNotFoundException(String message) {
        super(message);
    }
}
