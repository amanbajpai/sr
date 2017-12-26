package com.ros.smartrocket.db.bl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.db.entity.file.NotUploadedFile;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public final class FilesBL {

    private FilesBL() {
    }

    private static Cursor getFirstNotUploadedFileFromDB(long currentId, boolean use3GOnly) {
        String where = NotUploadedFileDbSchema.Columns._ID + ">'" + currentId + "'";
        if (use3GOnly) where += " and " + NotUploadedFileDbSchema.Columns.USE_3G + "==1";
        return App.getInstance().getContentResolver().query(NotUploadedFileDbSchema.CONTENT_URI,
                NotUploadedFileDbSchema.Query.PROJECTION, where, null, NotUploadedFileDbSchema.SORT_ORDER_ASC_LIMIT_1);
    }

    public static Observable<NotUploadedFile> firstNotUploadedFileObservable(long currentId, boolean use3GOnly) {
        return Observable.fromCallable(() -> convertCursorToNotUploadedFile(getFirstNotUploadedFileFromDB(currentId, use3GOnly)));
    }

    private static Cursor getNotUploadedFilesFromDB() {
        return App.getInstance().getContentResolver().query(NotUploadedFileDbSchema.CONTENT_URI,
                NotUploadedFileDbSchema.Query.PROJECTION, null, null, NotUploadedFileDbSchema.SORT_ORDER_DESC);
    }

    public static Observable<List<NotUploadedFile>> notUploadedFilesObservable() {
        return Observable.fromCallable(() -> convertCursorToNotUploadedFileList(getNotUploadedFilesFromDB()));
    }

    public static void updateShowNotificationStep(NotUploadedFile notUploadedFile) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NotUploadedFileDbSchema.Columns.SHOW_NOTIFICATION_STEP_ID.getName(),
                notUploadedFile.getShowNotificationStepId() + 1);
        resolver.update(NotUploadedFileDbSchema.CONTENT_URI, contentValues,
                NotUploadedFileDbSchema.Columns.ID + "=?", new String[]{String.valueOf(notUploadedFile.getId())});
    }

    private static Cursor getNotUploadedFilesCountFromDB() {
        return App.getInstance().getContentResolver()
                .query(NotUploadedFileDbSchema.CONTENT_URI, new String[]{"count(*)"}, null, null, null);
    }

    public static Observable<Integer> notNotUploadedFilesCountObservable() {
        return Observable.fromCallable(() -> getNotUploadedFilesCount(getNotUploadedFilesCountFromDB()));
    }

    private static Integer getNotUploadedFilesCount(Cursor cursor) {
        int count = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    // ------------- !!! -----------------//


    public static void deleteNotUploadedFileFromDbById(long id) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        resolver.delete(NotUploadedFileDbSchema.CONTENT_URI,
                NotUploadedFileDbSchema.Columns.ID + "=?", new String[]{String.valueOf(id)});
    }

    public static void insertNotUploadedFile(NotUploadedFile notUploadedFile) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        resolver.insert(NotUploadedFileDbSchema.CONTENT_URI, notUploadedFile.toContentValues());
    }

    public static void updatePortionAndFileCode(int id, int portion, String fileCode) {
        ContentResolver resolver = App.getInstance().getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotUploadedFileDbSchema.Columns.PORTION.getName(), portion);
        contentValues.put(NotUploadedFileDbSchema.Columns.FILE_CODE.getName(), fileCode);

        resolver.update(NotUploadedFileDbSchema.CONTENT_URI, contentValues,
                NotUploadedFileDbSchema.Columns.ID + "=?", new String[]{String.valueOf(id)});
    }

    public static int getNotUploadedFileCount(int taskId, int missionId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(NotUploadedFileDbSchema.CONTENT_URI, new String[]{"count(*)"},
                NotUploadedFileDbSchema.Columns.TASK_ID + " = ? and " +
                        NotUploadedFileDbSchema.Columns.MISSION_ID + " = ?",
                new String[]{String.valueOf(taskId), String.valueOf(missionId)}, null);

        int result = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
            cursor.close();
        }
        return result;
    }

    public static int getNotUploadedFileCount() {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(NotUploadedFileDbSchema.CONTENT_URI, new String[]{"count(*)"},
                null, null, null);

        int result = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
            cursor.close();
        }
        return result;
    }

    private static List<NotUploadedFile> convertCursorToNotUploadedFileList(Cursor cursor) {
        List<NotUploadedFile> result = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(NotUploadedFile.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    private static NotUploadedFile convertCursorToNotUploadedFile(Cursor cursor) {
        NotUploadedFile result = new NotUploadedFile();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result = NotUploadedFile.fromCursor(cursor);
            }
            cursor.close();
        }
        return result;
    }
}
