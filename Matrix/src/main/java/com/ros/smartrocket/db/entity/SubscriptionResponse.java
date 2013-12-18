package com.ros.smartrocket.db.entity;

public class SubscriptionResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

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
