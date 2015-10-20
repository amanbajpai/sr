package com.ros.smartrocket.db.entity;

import android.content.ContentValues;
import android.database.Cursor;

import com.ros.smartrocket.db.NotificationDbSchema;

/**
 * Created by macbook on 09.10.15.
 */
public class Notification extends BaseEntity {

    private Integer id;
    private String message;
    private Integer read;

    public Notification() {
    }

    public Notification(long _id, Integer id, String message, Integer read) {
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
            result.setRead(c.getInt(NotificationDbSchema.Query.READ));
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

}
