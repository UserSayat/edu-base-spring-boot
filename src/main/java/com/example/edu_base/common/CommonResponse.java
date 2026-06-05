package com.example.edu_base.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@JsonPropertyOrder({ "success", "errorCode", "data", "message", "details" })
public class CommonResponse<T> {

    private boolean success = true;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer errorCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
