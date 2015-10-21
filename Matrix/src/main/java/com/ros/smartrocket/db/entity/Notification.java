package com.ros.smartrocket.db.entity;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.NotificationDbSchema;

/**
 * Created by macbook on 09.10.15.
 */
public class Notification extends BaseEntity {

    private Integer id;

    @SerializedName("Text")
    private String message;

    @SerializedName("Subject")
    private String subject;

    private Boolean read = false;

    public Notification() {
    }

    public Notification(long _id, Integer id, String message, Boolean read) {
        set_id(_id);
        this.id = id;
        this.message = message;
        this.read = read;
    }

    public static Notification fromCursor(Cursor c) {
        Notification result = new Notification();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(NotificationDbSchema.Query._ID));
            result.setId(c.getInt(NotificationDbSchema.Query.ID));
            result.setMessage(c.getString(NotificationDbSchema.Query.MESSAGE));
            result.setMessage(c.getString(NotificationDbSchema.Query.SUBJECT));
            result.setRead(c.getInt(NotificationDbSchema.Query.READ) == 1);
        }

        return result;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
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

}
