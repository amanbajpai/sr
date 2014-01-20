package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.db.entity.NotUploadedFile;

import java.util.ArrayList;

/**
 * Created by bopr on 12/10/13.
 */
public class FilesBL {
    //private static final String TAG = TasksBL.class.getSimpleName();


    public FilesBL() {

    }

    public static void getNotUploadedFilesFromDB(AsyncQueryHandler handler) {
        handler.startQuery(NotUploadedFileDbSchema.Query.TOKEN_QUERY, null, NotUploadedFileDbSchema.CONTENT_URI,
                NotUploadedFileDbSchema.Query.PROJECTION, null, null, NotUploadedFileDbSchema.SORT_ORDER_DESC);
    }

    public static void getFirstNotUploadedFileFromDB(AsyncQueryHandler handler, long current_id, boolean useTreeGOnly) {
        String where = NotUploadedFileDbSchema.Columns._ID + ">'" + current_id + "'";
        if (useTreeGOnly) {
            where = where + " and " + NotUploadedFileDbSchema.Columns.USE_3G + "==1";
        }

        handler.startQuery(NotUploadedFileDbSchema.Query.TOKEN_QUERY, null, NotUploadedFileDbSchema.CONTENT_URI,
                NotUploadedFileDbSchema.Query.PROJECTION, where, null, NotUploadedFileDbSchema.SORT_ORDER_ASC_LIMIT_1);
    }

    public static void deleteNotUploadedFileFromDbById(long _id) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        resolver.delete(NotUploadedFileDbSchema.CONTENT_URI,
                NotUploadedFileDbSchema.Columns._ID + "=?", new String[]{String.valueOf(_id)});
    }

    public static void insertNotUploadedFile(NotUploadedFile notUploadedFile) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        resolver.insert(NotUploadedFileDbSchema.CONTENT_URI, notUploadedFile.toContentValues());
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
