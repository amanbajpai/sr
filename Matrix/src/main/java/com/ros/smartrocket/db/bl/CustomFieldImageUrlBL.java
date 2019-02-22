package com.ros.smartrocket.db.bl;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.CustomFieldImageUrlDbSchema;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.Table;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.question.CustomFieldImageUrls;
import com.ros.smartrocket.db.entity.question.Product;
import com.ros.smartrocket.db.entity.question.Question;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class CustomFieldImageUrlBL {

    private static Integer removeAnswersByTaskId(int taskId) {
        return App.getInstance().getContentResolver()
                .delete(CustomFieldImageUrlDbSchema.CONTENT_URI,
                        CustomFieldImageUrlDbSchema.Columns.TASK_ID + "=?",
                        new String[]{String.valueOf(taskId)});
    }

    private static Cursor getCustomFiledImageUrlListFromDB(Question question, Product product) {
        return App.getInstance().getContentResolver().query(
                CustomFieldImageUrlDbSchema.CONTENT_URI,
                CustomFieldImageUrlDbSchema.Query.PROJECTION,
                CustomFieldImageUrlDbSchema.Columns.QUESTION_ID + "=? and "
                        + CustomFieldImageUrlDbSchema.Columns.TASK_ID + "=? and "
                        + CustomFieldImageUrlDbSchema.Columns.MISSION_ID + "=? and "
                        + CustomFieldImageUrlDbSchema.Columns.PRODUCT_ID + "=?",
                new String[]{String.valueOf(question.getId()),
                        String.valueOf(question.getTaskId()),
                        String.valueOf(question.getMissionId()),
                        String.valueOf(product.getId())},
                CustomFieldImageUrlDbSchema.SORT_ORDER_ASC);
    }

    private static Cursor getCustomFiledImageUrlListFromDB(Question question) {
        return App.getInstance().getContentResolver().query(
                CustomFieldImageUrlDbSchema.CONTENT_URI,
                CustomFieldImageUrlDbSchema.Query.PROJECTION,
                CustomFieldImageUrlDbSchema.Columns.QUESTION_ID + "=? and "
                        + CustomFieldImageUrlDbSchema.Columns.TASK_ID + "=? and "
                        + CustomFieldImageUrlDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(question.getId()),
                        String.valueOf(question.getTaskId()),
                        String.valueOf(question.getMissionId())},
                CustomFieldImageUrlDbSchema.SORT_ORDER_ASC);
    }

    public static Observable<List<CustomFieldImageUrls>> getCustomFieldImageUrlFromDBObservable(Question question, Product product) {
        if (product != null && product.getId() != null)
            return Observable.fromCallable(() -> convertCursorToCustomFieldImageUrlList(getCustomFiledImageUrlListFromDB(question, product)));
        else
            return Observable.fromCallable(() -> convertCursorToCustomFieldImageUrlList(getCustomFiledImageUrlListFromDB(question)));
    }

    public static long insert(CustomFieldImageUrls imageUrls) {
        Uri uri = App.getInstance().getContentResolver().insert(CustomFieldImageUrlDbSchema.CONTENT_URI, imageUrls.toContentValues());
        return ContentUris.parseId(uri);
    }

    public static int updateCustomFieldImageUrlInDB(List<CustomFieldImageUrls> customFieldImageUrls) {
        int count = 0;
        for (CustomFieldImageUrls customFieldImageUrls1 : customFieldImageUrls) {
            count += App.getInstance()
                    .getContentResolver()
                    .update(CustomFieldImageUrlDbSchema.CONTENT_URI,
                            customFieldImageUrls1.toContentValues(),
                            CustomFieldImageUrlDbSchema.Columns._ID + "=?",
                            new String[]{String.valueOf(customFieldImageUrls1.get_id())});
        }
        return count;
    }

    private static int deleteCustomFieldImageUrlFromDB(CustomFieldImageUrls imageUrls) {
        return App.getInstance().getContentResolver().delete(CustomFieldImageUrlDbSchema.CONTENT_URI,
                CustomFieldImageUrlDbSchema.Columns._ID + "=?", new String[]{String.valueOf(imageUrls.get_id())});
    }

    // ------------------ !!!! ----------------- //

    public static void setMissionId(Integer taskId, Integer missionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CustomFieldImageUrlDbSchema.Columns.MISSION_ID.getName(), missionId);

        String where = CustomFieldImageUrlDbSchema.Columns.TASK_ID + "=? and (" +
                CustomFieldImageUrlDbSchema.Columns.MISSION_ID + "=? or " + CustomFieldImageUrlDbSchema.Columns.MISSION_ID + " IS NULL )";
        String[] whereArgs = new String[]{String.valueOf(taskId), String.valueOf(0)};

        App.getInstance().getContentResolver().update(CustomFieldImageUrlDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }


//    public static List<CustomFieldImageUrls> getCustomFieldImageUrlListToSend(Integer taskId, Integer missionId) {
//        ContentResolver resolver = App.getInstance().getContentResolver();
//        Cursor cursor = resolver.query(CustomFieldImageUrlDbSchema.CONTENT_URI, CustomFieldImageUrlDbSchema.Query.PROJECTION,
//                CustomFieldImageUrlDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.CHECKED + "=? and " +
//                        CustomFieldImageUrlDbSchema.Columns.MISSION_ID + "=? and " +
//                        CustomFieldImageUrlDbSchema.Columns.VALUE + " IS NOT NULL and " +
//                        CustomFieldImageUrlDbSchema.Columns.VALUE + " !=? ",
//                new String[]{String.valueOf(taskId), String.valueOf(1), String.valueOf(missionId), ""}, null);
//        return convertCursorToCustomFieldImageUrlList(cursor);
//    }


    static List<CustomFieldImageUrls> convertCursorToCustomFieldImageUrlList(Cursor cursor) {
        List<CustomFieldImageUrls> result = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(CustomFieldImageUrls.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    public static void updateCustomFieldImageUrl(int waveId, int questionId) {
        String where = CustomFieldImageUrlDbSchema.Columns.ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(waveId)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(CustomFieldImageUrlDbSchema.Columns.QUESTION_ID.getName(), questionId);
        App.getInstance().getContentResolver().update(CustomFieldImageUrlDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

}
