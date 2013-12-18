package com.ros.smartrocket.db.entity;

public class ResponseError extends BaseEntity {

    private static final long serialVersionUID = -2989395260527906044L;

    private String ErrorMessage;

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.ErrorMessage = errorMessage;
    }
}
