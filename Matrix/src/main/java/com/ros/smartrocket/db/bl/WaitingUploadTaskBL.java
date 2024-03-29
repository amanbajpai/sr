package com.ros.smartrocket.db.bl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.WaitingUploadTaskDbSchema;
import com.ros.smartrocket.db.entity.task.WaitingUploadTask;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public final class WaitingUploadTaskBL {

    private WaitingUploadTaskBL() {
    }

    private static Cursor getWaitingTasksFromDB() {
        return App.getInstance().getContentResolver().query(WaitingUploadTaskDbSchema.CONTENT_URI,
                WaitingUploadTaskDbSchema.Query.PROJECTION, WaitingUploadTaskDbSchema.Columns.ALL_FILE_SENT + " = ?",
                new String[]{"1"}, WaitingUploadTaskDbSchema.SORT_ORDER_DESC);
    }

    public static Observable<List<WaitingUploadTask>> waitingTasksObservable() {
        return Observable.fromCallable(() -> convertCursorToWaitingUploadTaskList(getWaitingTasksFromDB()));
    }

    /// -------------- !!! --------------///

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

    public static void deleteUploadedTaskFromDbById(int waveId, int taskId, int missionId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        resolver.delete(WaitingUploadTaskDbSchema.CONTENT_URI,
                WaitingUploadTaskDbSchema.Columns.WAVE_ID + "=? and " +
                        WaitingUploadTaskDbSchema.Columns.TASK_ID + "=? and " +
                        WaitingUploadTaskDbSchema.Columns.MISSION_ID +
                        "=?", new String[]{String.valueOf(waveId), String.valueOf(taskId), String.valueOf(missionId)});
    }

    public static WaitingUploadTask getWaitingUploadTask(int waveId, int taskId, int missionId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor c = resolver.query(WaitingUploadTaskDbSchema.CONTENT_URI, WaitingUploadTaskDbSchema.Query.PROJECTION, WaitingUploadTaskDbSchema.Columns.WAVE_ID + "=? and " +
                WaitingUploadTaskDbSchema.Columns.TASK_ID + "=? and " +
                WaitingUploadTaskDbSchema.Columns.MISSION_ID +
                "=?", new String[]{String.valueOf(waveId), String.valueOf(taskId), String.valueOf(missionId)}, null);
        return convertCursorToWaitingUploadTask(c);
    }

    public static void insertWaitingUploadTask(WaitingUploadTask waitingUploadTask) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        resolver.insert(WaitingUploadTaskDbSchema.CONTENT_URI, waitingUploadTask.toContentValues());
    }

    private static List<WaitingUploadTask> convertCursorToWaitingUploadTaskList(Cursor cursor) {
        List<WaitingUploadTask> result = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(WaitingUploadTask.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    private static WaitingUploadTask convertCursorToWaitingUploadTask(Cursor cursor) {
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
