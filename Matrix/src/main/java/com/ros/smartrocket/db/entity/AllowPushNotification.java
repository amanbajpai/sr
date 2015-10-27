package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by macbook on 21.10.15.
 */
public class AllowPushNotification extends BaseEntity {

    @SerializedName("AllowPushNotification")
    private Boolean allow;

    public void allow(){
        allow = true;
    }

    public void disallow(){
        allow = false;
    }

    public Boolean getAllow() {
        return allow;
    }
}
