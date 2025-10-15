package com.vani.week4.backend.global;

import com.vani.week4.backend.global.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author vani
 * @since 10/13/25
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        ErrorResponse response = new ErrorResponse("INTERNAL_SERVER_ERROR", "서버가 아파요");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex){
        ErrorResponse response = new ErrorResponse("EMAIL_DUPLICATED", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NicknameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleNicknameAlreadyExists(NicknameAlreadyExistsException ex){
        ErrorResponse response = new ErrorResponse("NICKNAME_DUPLICATED", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex){
        ErrorResponse response = new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex){
        ErrorResponse response = new ErrorResponse("INVALID_PASSWORD", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleUserAccessDenied(UserAccessDeniedException ex){
        ErrorResponse response = new ErrorResponse("ACCESS_DENIED", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthNotFound(AuthNotFoundException ex){
        ErrorResponse response = new ErrorResponse("AUTH_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex){
        ErrorResponse response = new ErrorResponse("INVALID_TOKEN", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException ex){
        ErrorResponse response = new ErrorResponse("POST_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex){
        ErrorResponse response = new ErrorResponse("UNAUTHORIZED", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

}
