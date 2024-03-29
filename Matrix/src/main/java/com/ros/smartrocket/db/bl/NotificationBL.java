package com.ros.smartrocket.db.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.NotificationDbSchema;
import com.ros.smartrocket.db.entity.Notification;
import com.ros.smartrocket.utils.L;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class NotificationBL {

    private NotificationBL() {
    }

    public static Cursor getUnreadNotificationsFromDB() {
        return App.getInstance().getContentResolver().query(NotificationDbSchema.CONTENT_URI,
                NotificationDbSchema.Query.PROJECTION, NotificationDbSchema.Columns.READ + "=?",
                new String[]{String.valueOf(0)}, NotificationDbSchema.SORT_ORDER_DESC);
    }

    public static Observable<Integer> unreadNotificationsObservable() {
        return Observable.fromCallable(() -> convertCursorToUnreadNotificationsCount(getUnreadNotificationsFromDB()));
    }

    // --------------- !!!! --------------- //

    public static void getNotificationsFromDB(AsyncQueryHandler handler) {
        handler.startQuery(NotificationDbSchema.Query.TOKEN_QUERY, null, NotificationDbSchema.CONTENT_URI,
                NotificationDbSchema.Query.PROJECTION, null,
                null, NotificationDbSchema.SORT_ORDER_DESC);
    }

    public static List<Notification> convertCursorToNotificationList(Cursor cursor) {
        List<Notification> result = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext())
                result.add(Notification.fromCursor(cursor));
            cursor.close();
        }
        return result;
    }

    private static int convertCursorToUnreadNotificationsCount(Cursor cursor) {
        int result = 0;
        if (cursor != null) result = cursor.getCount();
        return result;
    }

    public static void saveNotification(ContentResolver contentResolver, Notification notification) {
        if (notification.getTimestamp() == 0)
            notification.setTimestamp(System.currentTimeMillis());
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

    public static void removeAllNotifications(Context context) {
        context.getContentResolver().delete(NotificationDbSchema.CONTENT_URI, null, null);
    }
}
