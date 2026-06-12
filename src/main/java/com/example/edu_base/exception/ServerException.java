package com.example.edu_base.exception;

import java.util.List;

public class ServerException extends RuntimeException {
    private final Integer errorCode;
    private final List<String> details;


    public ServerException(String message, Integer errorCode, List<String> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public ServerException(String message, Throwable cause, Integer errorCode, List<String> details) {
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
