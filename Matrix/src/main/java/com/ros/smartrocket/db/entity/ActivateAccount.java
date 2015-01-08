package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class ActivateAccount extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("Email")
    private String email;
    @SerializedName("Token")
    private String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
