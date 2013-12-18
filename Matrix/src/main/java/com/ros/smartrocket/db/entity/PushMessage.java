package com.ros.smartrocket.db.entity;

public class PushMessage extends BaseEntity{

    private String Message;
    private String TargetDeviceId;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTargetDeviceId() {
        return TargetDeviceId;
    }

    public void setTargetDeviceId(String targetDeviceId) {
        TargetDeviceId = targetDeviceId;
    }
}
