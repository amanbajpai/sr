package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class LoginResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("Token")
    private String token;
    @SerializedName("ShowTermsConditions")
    private boolean showTermsConditions;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isShowTermsConditions() {
        return showTermsConditions;
    }

    public void setShowTermsConditions(boolean showTermsConditions) {
        this.showTermsConditions = showTermsConditions;
    }
}
