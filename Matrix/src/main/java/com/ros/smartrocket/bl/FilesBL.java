package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.db.entity.NotUploadedFile;

import java.util.ArrayList;

public class FilesBL {
    //private static final String TAG = TasksBL.class.getSimpleName();


    public FilesBL() {

    }

    public static void getNotUploadedFilesFromDB(AsyncQueryHandler handler, String cookie) {
        handler.startQuery(NotUploadedFileDbSchema.Query.TOKEN_QUERY, cookie, NotUploadedFileDbSchema.CONTENT_URI,
                NotUploadedFileDbSchema.Query.PROJECTION, null, null, NotUploadedFileDbSchema.SORT_ORDER_DESC);
    }

    public static void getNotUploadedFilesCountFromDB(AsyncQueryHandler handler, String cookie) {
        handler.startQuery(NotUploadedFileDbSchema.Query.TOKEN_QUERY, cookie, NotUploadedFileDbSchema.CONTENT_URI,
                new String[] { "count(*)" }, null, null, null);
    }

    public static void getFirstNotUploadedFileFromDB(AsyncQueryHandler handler, long currentId,
                                                     boolean useTreeGOnly, String cookie) {
        String where = NotUploadedFileDbSchema.Columns._ID + ">'" + currentId + "'";
        if (useTreeGOnly) {
            where = where + " and " + NotUploadedFileDbSchema.Columns.USE_3G + "==1";
        }

        handler.startQuery(NotUploadedFileDbSchema.Query.TOKEN_QUERY, cookie, NotUploadedFileDbSchema.CONTENT_URI,
                NotUploadedFileDbSchema.Query.PROJECTION, where, null, NotUploadedFileDbSchema.SORT_ORDER_ASC_LIMIT_1);
    }

    public static void updateShowNotificationStep(NotUploadedFile notUploadedFile) {
        ContentResolver resolver = App.getInstance().getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NotUploadedFileDbSchema.Columns.SHOW_NOTIFICATION_STEP_ID.getName(),
                notUploadedFile.getShowNotificationStepId() + 1);

        resolver.update(NotUploadedFileDbSchema.CONTENT_URI, contentValues,
                NotUploadedFileDbSchema.Columns.ID + "=?", new String[]{String.valueOf(notUploadedFile.getId())});
    }

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

    public static int getNotUploadedFileCount(int taskId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(NotUploadedFileDbSchema.CONTENT_URI, new String[]{"count(*)"},
                NotUploadedFileDbSchema.Columns.TASK_ID + " = ?", new String[]{String.valueOf(taskId)}, null);

        int result = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
            cursor.close();
        }
        return result;
    }


    /**
     * Convert cursor to NotUploadedFile list
     *
     * @param cursor - all fields cursor
     * @return
     */
    public static ArrayList<NotUploadedFile> convertCursorToNotUploadedFileList(Cursor cursor) {
        ArrayList<NotUploadedFile> result = new ArrayList<NotUploadedFile>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(NotUploadedFile.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Convert cursor to NotUploadedFile
     *
     * @param cursor - all fields cursor
     * @return
     */
    public static NotUploadedFile convertCursorToNotUploadedFile(Cursor cursor) {
        NotUploadedFile result = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result = NotUploadedFile.fromCursor(cursor);
            }
            cursor.close();
        }
        return result;
    }
}
