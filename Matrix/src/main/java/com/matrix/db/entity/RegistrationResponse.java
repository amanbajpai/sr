package com.matrix.db.entity;

public class RegistrationResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    private String Token;
    private Boolean State = false;
    private String Message;

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }

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
