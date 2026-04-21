package com.example.edu_base.common;

import java.util.List;

public class ServerException extends Exception {
    private Integer errorCode;
    private List<String> details;


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
