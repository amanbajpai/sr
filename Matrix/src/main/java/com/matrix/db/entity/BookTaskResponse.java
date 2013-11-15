package com.matrix.db.entity;

public class BookTaskResponse extends BaseEntity {
    private static final long serialVersionUID = -2698817074641059131L;

    private Boolean State = false;
    private String Message;

    public Boolean getState() {
        return State;
    }

    public void setState(Boolean state) {
        this.State = state;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}
