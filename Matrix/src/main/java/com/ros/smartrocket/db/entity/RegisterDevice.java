package com.ros.smartrocket.db.entity;

public class RegisterDevice extends BaseEntity{

    private String DeviceId;
    private String RegistrationId;

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getRegistrationId() {
        return RegistrationId;
    }

    public void setRegistrationId(String registrationId) {
        RegistrationId = registrationId;
    }
}
