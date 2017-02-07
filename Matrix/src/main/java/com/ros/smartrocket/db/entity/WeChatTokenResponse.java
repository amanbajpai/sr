package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class WeChatTokenResponse extends BaseEntity {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("openid")
    private String openId;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
