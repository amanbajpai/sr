package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class ResponseError extends BaseEntity {
    private static final long serialVersionUID = -2989395260527906044L;

    @SerializedName("ErrorCode")
    private Integer errorCode;
    @SerializedName("ErrorMessage")
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
