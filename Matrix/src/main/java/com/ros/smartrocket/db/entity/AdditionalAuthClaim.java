package com.ros.smartrocket.db.entity;

public class AdditionalAuthClaim {
    private String openid;

    public AdditionalAuthClaim(String openid) {
        this.openid = openid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
