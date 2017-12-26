package com.ros.smartrocket.db.entity.account;

import com.ros.smartrocket.db.entity.BaseEntity;

public class ExternalAuthResponse extends BaseEntity {
    private String token;
    private boolean showTermsConditions;
    private boolean registrationRequested;

    public boolean isShowTermsConditions() {
        return showTermsConditions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRegistrationRequested() {
        return registrationRequested;
    }
}
