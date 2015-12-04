package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.NotificationDbSchema;

/**
 * Created by macbook on 09.10.15.
 */
public class Notification extends BaseEntity {

    @SerializedName("Text")
    private String message;

    @SerializedName("Subject")
    private String subject;

    private Boolean read = false;
    private Long timestamp = (long) 0;

    public Notification() {
    }

    public Notification(String message, Boolean read) {
        this.message = message;
        this.read = read;
    }

    public static Notification fromCursor(Cursor c) {
        Notification result = new Notification();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(NotificationDbSchema.Query._ID));
            result.setId(c.getInt(NotificationDbSchema.Query._ID));
            result.setMessage(c.getString(NotificationDbSchema.Query.MESSAGE));
            result.setSubject(c.getString(NotificationDbSchema.Query.SUBJECT));
            result.setRead(c.getInt(NotificationDbSchema.Query.READ) == 1);
            result.setTimestamp(c.getLong(NotificationDbSchema.Query.TIMESTAMP));
        }

        return result;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
