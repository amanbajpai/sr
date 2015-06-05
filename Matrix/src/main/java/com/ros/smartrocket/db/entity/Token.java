package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Token extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("Token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
