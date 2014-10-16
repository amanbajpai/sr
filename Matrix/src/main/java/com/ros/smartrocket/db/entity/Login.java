package com.ros.smartrocket.db.entity;

public class Login extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Email;
    private String Password;
    private String DeviceName;
    private String AppVersion;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }


}
