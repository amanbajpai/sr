package com.ros.smartrocket.db.entity;

public class ExternalAuthResponse extends BaseEntity {
    private String token;
    private boolean showTermsConditions;
    private boolean registrationRequested;

    public boolean isShowTermsConditions() {
        return showTermsConditions;
    }

    public void setShowTermsConditions(boolean showTermsConditions) {
        this.showTermsConditions = showTermsConditions;
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

    public void setRegistrationRequested(boolean registrationRequested) {
        this.registrationRequested = registrationRequested;
    }
}
