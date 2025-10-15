package com.vani.week4.backend.global.exception;

/**
 * @author vani
 * @since 10/13/25
 */
public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(String message) {
        super(message);
    }
}
