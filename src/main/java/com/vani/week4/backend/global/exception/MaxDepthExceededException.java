package com.vani.week4.backend.global.exception;

/**
 * @author vani
 * @since 10/13/25
 */
public class MaxDepthExceededException extends RuntimeException{
    public MaxDepthExceededException(String message) {
        super(message);
    }
}
