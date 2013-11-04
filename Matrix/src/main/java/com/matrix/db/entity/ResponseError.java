package com.matrix.db.entity;

public class ResponseError extends BaseEntity {

    private static final long serialVersionUID = -2989395260527906044L;

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
