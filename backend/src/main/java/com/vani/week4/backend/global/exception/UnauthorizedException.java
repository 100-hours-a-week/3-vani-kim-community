package com.vani.week4.backend.global.exception;

/**
 * @author vani
 * @since 10/13/25
 */
public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
