package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class LoginResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("Token")
    private String token;
    @SerializedName("State")
    private Boolean state = false;

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
}
