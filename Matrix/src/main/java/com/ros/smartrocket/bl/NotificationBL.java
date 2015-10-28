package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.NotificationDbSchema;
import com.ros.smartrocket.db.entity.Notification;
import com.ros.smartrocket.utils.L;

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

    public static int convertCursorToUnreadNotificationsCount(Cursor cursor) {
        int result = 0;
        if (cursor != null) {
            result = cursor.getCount();
        }

        return result;
    }

    public static ArrayList<Notification> createFakeNotifications() {
        ArrayList<Notification> result = new ArrayList<>();
        result.add(new Notification("test1", true));
        result.add(new Notification("test2 test2 test2 test2 test2 test2 ", false));
        result.add(new Notification("test3 test3 test3 test3 test3 test3 ", true));
        result.add(new Notification("test4 test4 test4 test4 test4 test4 ", false));
        result.add(new Notification("test5 test5 test5 test5 test5 test5 ", false));
        return result;
    }

    public static void saveNotification(ContentResolver contentResolver, Notification notification) {
        if (notification.getTimestamp() == 0) {
            notification.setTimestamp(System.currentTimeMillis());
        }
        contentResolver.insert(NotificationDbSchema.CONTENT_URI, notification.toContentValues());
        L.i("NOTIFICATION BL", "INSERTED");
    }

    public static void updateNotification(ContentResolver contentResolver, Notification notification) {
        contentResolver.update(NotificationDbSchema.CONTENT_URI, notification.toContentValues(),
                NotificationDbSchema.Columns._ID + "=?",
                new String[]{String.valueOf(notification.get_id())});
        L.i("NOTIFICATION BL", "UPDATED");
    }

    public static void deleteNotification(ContentResolver contentResolver, long notifId) {
        contentResolver.delete(NotificationDbSchema.CONTENT_URI,
                NotificationDbSchema.Columns._ID + "=?",
                new String[]{String.valueOf(notifId)});
        L.i("NOTIFICATION BL", "REMOVED");
    }

    public static void getNotificationFromDB(AsyncQueryHandler handler, long notifId) {
        handler.startQuery(NotificationDbSchema.Query.TOKEN_QUERY, null, NotificationDbSchema.CONTENT_URI,
                NotificationDbSchema.Query.PROJECTION, NotificationDbSchema.Columns._ID + "=?",
                new String[]{String.valueOf(notifId)}, NotificationDbSchema.SORT_ORDER_ASC_LIMIT_1);
    }

    public static void getNotificationsFromDB(AsyncQueryHandler handler) {
        handler.startQuery(NotificationDbSchema.Query.TOKEN_QUERY, null, NotificationDbSchema.CONTENT_URI,
                NotificationDbSchema.Query.PROJECTION, null,
                null, NotificationDbSchema.SORT_ORDER_DESC);
    }

    public static void getUnreadNotificationsFromDB(AsyncQueryHandler handler) {
        handler.startQuery(NotificationDbSchema.Query.TOKEN_QUERY, null, NotificationDbSchema.CONTENT_URI,
                NotificationDbSchema.Query.PROJECTION, NotificationDbSchema.Columns.READ + "=?",
                new String[]{String.valueOf(0)}, NotificationDbSchema.SORT_ORDER_DESC);
    }

    public static Cursor getUnreadNotificationsFromDB(ContentResolver contentResolver) {
        return contentResolver.query(NotificationDbSchema.CONTENT_URI,
                NotificationDbSchema.Query.PROJECTION, NotificationDbSchema.Columns.READ + "=?",
                new String[]{String.valueOf(0)}, NotificationDbSchema.SORT_ORDER_DESC);
    }

    public static void removeAllNotifications(Context context) {
        context.getContentResolver().delete(NotificationDbSchema.CONTENT_URI, null, null);
    }
}
