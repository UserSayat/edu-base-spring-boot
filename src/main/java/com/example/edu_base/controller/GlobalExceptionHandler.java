package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.exception.ServerException;
import com.example.edu_base.exception.EntityNotFoundException;
import com.example.edu_base.exception.UnauthenticatedException;
import com.example.edu_base.exception.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        log.warn("request could not be processed due to invalid parameters of request body: {}", details);

        CommonResponse<?> response = new CommonResponse<>(10001, "validation error in request body", details);
        response.setSuccess(false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<?>> handleConstraintViolation(ConstraintViolationException e) {
        List<String> details = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        log.warn("request could not be processed due to invalid parameters of path variables or request params: {}", details);

        CommonResponse<?> response = new CommonResponse<>(10002, "validation error of request params or path variables", details);
        response.setSuccess(false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();

        log.warn("invalid arguments passed, {}", message);

        CommonResponse<?> response = new CommonResponse<>(1004, message, null);
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();

        log.warn("requested resource is missing, {}", message);

        CommonResponse<?> response = new CommonResponse<>(10005, message, null);
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<CommonResponse<?>> handleServerException(ServerException e) {
        log.error("database interaction failure while performing an operation: ", e);
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
        CommonResponse<?> response = new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails());
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(response);
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<CommonResponse<?>> handleUnauthenticatedException(UnauthenticatedException e) {
        log.error("authentication error: ", e);
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
        CommonResponse<?> response = new CommonResponse<>(10006, message, null);
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CommonResponse<?>> handleUnauthorizedException(UnauthorizedException e) {
        log.error("authorization error: ", e);
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
        CommonResponse<?> response = new CommonResponse<>(10007, message, null);
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        log.error("unexpected error occurred while processing request: ", e);
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
        CommonResponse<?> response = new CommonResponse<>(10008, message, null);
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
