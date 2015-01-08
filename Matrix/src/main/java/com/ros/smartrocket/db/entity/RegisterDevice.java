package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class RegisterDevice extends BaseEntity {

    @SerializedName("DeviceId")
    private String deviceId;
    @SerializedName("RegistrationId")
    private String registrationId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
