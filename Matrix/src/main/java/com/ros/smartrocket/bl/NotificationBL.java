package com.ros.smartrocket.bl;

import android.database.Cursor;

import com.ros.smartrocket.db.entity.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbook on 09.10.15.
 */
public class NotificationBL {

    private NotificationBL() {

    }

    /**
     * Convert cursor to Answer list
     *
     * @param cursor - all fields cursor
     * @return ArrayList<Notification>
     */
    public static List<Notification> convertCursorToNotificationList(Cursor cursor) {
        List<Notification> result = new ArrayList<Notification>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Notification.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    public static ArrayList<Notification> createFakeNotifications() {
        ArrayList<Notification> result = new ArrayList<>();
        result.add(new Notification(1, 1, "test1", 1));
        result.add(new Notification(2, 2, "test2 test2 test2 test2 test2 test2 ", 0));
        result.add(new Notification(3, 3, "test3 test3 test3 test3 test3 test3 ", 1));
        result.add(new Notification(4, 4, "test4 test4 test4 test4 test4 test4 ", 1));
        result.add(new Notification(5, 5, "test5 test5 test5 test5 test5 test5 ", 0));
        return result;
    }
}
