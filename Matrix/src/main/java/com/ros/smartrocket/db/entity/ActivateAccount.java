package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class ActivateAccount extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("Email")
    private String email;
    @SerializedName("Token")
    private String token;

    public ActivateAccount(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
