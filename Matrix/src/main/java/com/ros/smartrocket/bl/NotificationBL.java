package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.database.Cursor;

import com.ros.smartrocket.db.AnswerDbSchema;
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
     * Make request for getting Answer list
     *
     * @param handler    - Handler for getting response from DB
     * @param questionId - question id
     */
    public static void getAnswersListFromDB(AsyncQueryHandler handler, Integer taskId, Integer missionId, Integer
            questionId) {
        handler.startQuery(AnswerDbSchema.Query.TOKEN_QUERY, null, AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Query.PROJECTION, AnswerDbSchema.Columns.QUESTION_ID + "=? and " + AnswerDbSchema
                        .Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(questionId), String.valueOf(taskId), String.valueOf(missionId)}, AnswerDbSchema.SORT_ORDER_ASC
        );
    }

    /**
     * Convert cursor to Answer list
     *
     * @param cursor - all fields cursor
     * @return ArrayList<Answer>
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

    public static List<Notification> createFakeNotifications() {
        ArrayList<Notification> result = new ArrayList<>();
        result.add(new Notification(1, 1, "test1", 1));
        result.add(new Notification(2, 2, "test2 test2 test2 test2 test2 test2 ", 0));
        result.add(new Notification(3, 3, "test3 test3 test3 test3 test3 test3 ", 1));
        result.add(new Notification(4, 4, "test4 test4 test4 test4 test4 test4 ", 1));
        result.add(new Notification(5, 5, "test5 test5 test5 test5 test5 test5 ", 0));
        return result;
    }
}
