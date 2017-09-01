package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse extends BaseEntity {
    private static final long serialVersionUID = -2989395260527906044L;

    @SerializedName("ErrorCode")
    private Integer errorCode;
    @SerializedName("ErrorMessage")
    private String errorMessage;
    @SerializedName("Data")
    private ErrorData data;

    public String getErrorMessage() {
        return errorMessage == null ? "" : errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode == null ? -1 : errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorData getData() {
        return data == null ? new ErrorData() : data;
    }

    public void setData(ErrorData data) {
        this.data = data;
    }
}
