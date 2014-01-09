package com.ros.smartrocket.db.entity;

public class ResponseError extends BaseEntity {
    private static final long serialVersionUID = -2989395260527906044L;

    private Integer ErrorCode;
    private String ErrorMessage;

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.ErrorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(Integer errorCode) {
        ErrorCode = errorCode;
    }
}
