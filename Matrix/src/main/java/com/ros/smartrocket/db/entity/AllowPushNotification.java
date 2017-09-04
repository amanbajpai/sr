package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class AllowPushNotification extends BaseEntity {

    @SerializedName("AllowPushNotification")
    private Boolean allow;

    public AllowPushNotification(Boolean allow) {
        this.allow = allow;
    }

    public Boolean getAllow() {
        return allow;
    }
}
