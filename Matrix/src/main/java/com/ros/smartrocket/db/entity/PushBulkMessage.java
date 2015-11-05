package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by macbook on 21.10.15.
 */
public class PushBulkMessage extends BaseEntity {

    @SerializedName("MessageSubject")
    private String subject;

    @SerializedName("MessageText")
    private String text;

    @SerializedName("Settings")
    private PushSettings settings;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PushSettings getSettings() {
        return settings;
    }

    public void setSettings(PushSettings settings) {
        this.settings = settings;
    }
}
