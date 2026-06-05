package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.common.ServerException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        CommonResponse<?> response = new CommonResponse<>(10003, "validation error in request body", details);
        response.setSuccess(false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<?>> handleConstraintViolation(ConstraintViolationException e) {
        List<String> details = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        CommonResponse<?> response = new CommonResponse<>(10002, "validation error of request params or path variables", details);
        response.setSuccess(false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CommonResponse<?>> handleValidationException(ValidationException e) {
        CommonResponse<?> response = new CommonResponse<>(10000, "validation error on server side", null);
        response.setSuccess(false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        CommonResponse<?> response = new CommonResponse<>(10001, "not found", null);
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<CommonResponse<?>> handleServerException(ServerException e) {
        CommonResponse<?> response = new CommonResponse<>(e.getErrorCode(), e.getMessage(), e.getDetails());
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        CommonResponse<?> response = new CommonResponse<>(10004, e.getMessage(), null);
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
