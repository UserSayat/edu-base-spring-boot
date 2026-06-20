package com.example.edu_base.exception;

import java.util.List;

public class UnauthorizedException extends RuntimeException {
    private Integer errorCode;
    private List<String> details;


    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Integer errorCode, List<String> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public UnauthorizedException(String message, Throwable cause, Integer errorCode, List<String> details) {
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
