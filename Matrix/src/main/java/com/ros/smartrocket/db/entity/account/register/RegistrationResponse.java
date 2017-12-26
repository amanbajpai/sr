package com.ros.smartrocket.db.entity.account.register;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class RegistrationResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("Token")
    private String token;
    @SerializedName("State")
    private Boolean state = false;
    @SerializedName("Message")
    private String message;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
