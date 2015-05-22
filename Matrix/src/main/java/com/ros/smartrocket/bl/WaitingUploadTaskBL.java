package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.db.WaitingUploadTaskDbSchema;
import com.ros.smartrocket.db.entity.WaitingUploadTask;

import java.util.ArrayList;
import java.util.List;

public class WaitingUploadTaskBL {

    private WaitingUploadTaskBL() {

    }

    public static void getUploadedTasksFromDB(AsyncQueryHandler handler) {
        handler.startQuery(WaitingUploadTaskDbSchema.Query.TOKEN_QUERY, null, WaitingUploadTaskDbSchema.CONTENT_URI,
                WaitingUploadTaskDbSchema.Query.PROJECTION, WaitingUploadTaskDbSchema.Columns.ALL_FILE_SENT + " = ?",
                new String[]{"1"}, NotUploadedFileDbSchema.SORT_ORDER_DESC);
    }

    public static void updateStatusToAllFileSent(int waveId, int taskId, int missionId) {
        ContentResolver resolver = App.getInstance().getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(WaitingUploadTaskDbSchema.Columns.ALL_FILE_SENT.getName(), true);

        resolver.update(WaitingUploadTaskDbSchema.CONTENT_URI, contentValues,
                WaitingUploadTaskDbSchema.Columns.WAVE_ID + "=? and " +
                        WaitingUploadTaskDbSchema.Columns.TASK_ID + "=? and " +
                        WaitingUploadTaskDbSchema.Columns.MISSION_ID +
                        "=?", new String[]{String.valueOf(waveId), String.valueOf(taskId), String.valueOf(missionId)});
    }

    public static void deletUploadedTaskFromDbById(int waveId, int taskId, int missionId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        resolver.delete(WaitingUploadTaskDbSchema.CONTENT_URI,
                WaitingUploadTaskDbSchema.Columns.WAVE_ID + "=? and " +
                        WaitingUploadTaskDbSchema.Columns.TASK_ID + "=? and " +
                        WaitingUploadTaskDbSchema.Columns.MISSION_ID +
                        "=?", new String[]{String.valueOf(waveId), String.valueOf(taskId), String.valueOf(missionId)});
    }

    public static void insertWaitingUploadTask(WaitingUploadTask waitingUploadTask) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        resolver.insert(WaitingUploadTaskDbSchema.CONTENT_URI, waitingUploadTask.toContentValues());
    }

    /**
     * Convert cursor to WaitingUploadTask list
     *
     * @param cursor - all fields cursor
     * @return ArrayList
     */
    public static List<WaitingUploadTask> convertCursorToWaitingUploadTaskList(Cursor cursor) {
        List<WaitingUploadTask> result = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(WaitingUploadTask.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Convert cursor to WaitingUploadTask
     *
     * @param cursor - all fields cursor
     * @return WaitingUploadTask
     */
    public static WaitingUploadTask convertCursorToWaitingUploadTask(Cursor cursor) {
        WaitingUploadTask result = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result = WaitingUploadTask.fromCursor(cursor);
            }
            cursor.close();
        }
        return result;
    }
}
