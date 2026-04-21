package com.example.edu_base.common;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class CommonResponse<T> {
    private Integer errorCode;
    private T data;
    private String message;
    private List<String> details;

    public CommonResponse(T data) {
        this.data = data;
    }

    public CommonResponse(Integer errorCode, String message, List<String> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }
}
