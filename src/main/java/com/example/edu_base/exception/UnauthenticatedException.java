package com.example.edu_base.exception;

import java.util.List;

public class UnauthenticatedException extends RuntimeException {
    private Integer errorCode;
    private List<String> details;


    public UnauthenticatedException(String message) {
        super(message);
    }

    public UnauthenticatedException(String message, Integer errorCode, List<String> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public UnauthenticatedException(String message, Throwable cause, Integer errorCode, List<String> details) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public List<String> getDetails() {
        return details;
    }
}
