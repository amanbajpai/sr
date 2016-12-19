package com.ros.smartrocket.db.entity;

public class AppVersion extends BaseEntity {
    private String latestVersion;
    private String latestVersionLink;

    public AppVersion(String latestVersion) {
        this.latestVersion = latestVersion;
        latestVersionLink = "";
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getLatestVersionLink() {
        return latestVersionLink;
    }

    public void setLatestVersionLink(String latestVersionLink) {
        this.latestVersionLink = latestVersionLink;
    }
}
