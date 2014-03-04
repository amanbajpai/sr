package com.ros.smartrocket.bl;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.utils.UIUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AnswersBL {

    public AnswersBL() {

    }

    /**
     * Make request for getting Answer list
     *
     * @param handler - Handler for getting response from DB
     * @param questionId - question id
     */

    public static void getAnswersListFromDB(AsyncQueryHandler handler, Integer taskId, Integer questionId) {
        handler.startQuery(AnswerDbSchema.Query.TOKEN_QUERY, null, AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Query.PROJECTION, AnswerDbSchema.Columns.QUESTION_ID + "=? and " + AnswerDbSchema
                .Columns.TASK_ID + "=?",
                new String[]{String.valueOf(questionId), String.valueOf(taskId)}, AnswerDbSchema.SORT_ORDER_DESC);
    }

    public static void setAnswersToDB(AsyncQueryHandler handler, Answer[] answers) {
        for (Answer answer : answers) {
            handler.startUpdate(AnswerDbSchema.Query.TOKEN_UPDATE, null, AnswerDbSchema.CONTENT_URI,
                    answer.toContentValues(), AnswerDbSchema.Columns._ID + "=?",
                    new String[]{String.valueOf(answer.get_id())});
        }
    }

    public static ArrayList<NotUploadedFile> getTaskFilesListToUpload(Integer taskId, long endDateTime) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(AnswerDbSchema.CONTENT_URI, AnswerDbSchema.Query.PROJECTION,
                AnswerDbSchema.Columns.TASK_ID + "=?",
                new String[]{String.valueOf(taskId)}, null);

        Answer[] answers = convertCursorToAnswersArray(cursor);

        ArrayList<NotUploadedFile> notUploadedFiles = new ArrayList<NotUploadedFile>();
        for (Answer answer : answers) {
            if (!TextUtils.isEmpty(answer.getFileUri())) {
                NotUploadedFile fileToUpload = new NotUploadedFile();
                fileToUpload.setRandomId();
                fileToUpload.setTaskId(answer.getTaskId());
                fileToUpload.setQuestionId(answer.getQuestionId());
                fileToUpload.setFileUri(answer.getFileUri());
                fileToUpload.setFileSizeB(answer.getFileSizeB());
                fileToUpload.setFileName(answer.getFileName());
                fileToUpload.setShowNotificationStepId(0);
                fileToUpload.setPortion(0);
                fileToUpload.setAddedToUploadDateTime(UIUtils.getCurrentTimeInMilliseconds());
                fileToUpload.setEndDateTime(endDateTime);

                notUploadedFiles.add(fileToUpload);
            }
        }
        return notUploadedFiles;
    }


    public static float getTaskFilesSizeMb(ArrayList<NotUploadedFile> filesToUpload) {
        long resultSizeInB = 0;
        for (NotUploadedFile fileToUpload : filesToUpload) {
            resultSizeInB = resultSizeInB + fileToUpload.getFileSizeB();
        }

        return resultSizeInB / (float) 1024;
    }

    public String size(int size) {
        String hrSize;
        double m = size / 1024.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }

    public static ArrayList<Answer> getAnswersListToSend(Integer taskId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(AnswerDbSchema.CONTENT_URI, AnswerDbSchema.Query.PROJECTION,
                AnswerDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.CHECKED + "=?"/* and " + AnswerDbSchema
                        .Columns.FILE_URI + " IS NULL"*/,
                new String[]{String.valueOf(taskId), String.valueOf(1)}, null);

        return convertCursorToAnswerList(cursor);
    }

    public static ArrayList<Answer> getAnswersWithFilesListToSend(Integer taskId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(AnswerDbSchema.CONTENT_URI, AnswerDbSchema.Query.PROJECTION,
                AnswerDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.CHECKED + "=? and " + AnswerDbSchema
                        .Columns.FILE_URI + " NOT NULL",
                new String[]{String.valueOf(taskId), String.valueOf(1)}, null);

        return convertCursorToAnswerList(cursor);
    }

    /**
     * Convert cursor to Answer array
     *
     * @param cursor - all fields cursor
     * @return Answer[]
     */
    public static Answer[] convertCursorToAnswersArray(Cursor cursor) {
        Answer[] result = new Answer[]{};
        if (cursor != null) {
            result = new Answer[cursor.getCount()];

            while (cursor.moveToNext()) {
                result[cursor.getPosition()] = Answer.fromCursor(cursor);
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Convert cursor to Answer list
     *
     * @param cursor - all fields cursor
     * @return ArrayList<Answer>
     */
    public static ArrayList<Answer> convertCursorToAnswerList(Cursor cursor) {
        ArrayList<Answer> result = new ArrayList<Answer>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Answer.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }


    /**
     * Get next question orderId by answer routing.
     *
     * @param question - question id
     * @return int
     */
    public static int getNextQuestionOrderId(Question question) {
        int orderId = 0;
        if (question.getAnswers() != null) {
            for (Answer answer : question.getAnswers()) {
                if (answer.isChecked()) {
                    orderId = answer.getRouting();
                    if (orderId == 0) {
                        orderId = question.getOrderId() + 1;
                    }
                    break;
                }
            }
        }
        if (orderId == 0) {
            orderId = question.getOrderId() + 1;
        }
        return orderId;
    }

    public static void clearTaskUserAnswers(Activity activity, int taskId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerDbSchema.Columns.CHECKED.getName(), false);
        contentValues.putNull(AnswerDbSchema.Columns.FILE_URI.getName());

        activity.getContentResolver().update(AnswerDbSchema.CONTENT_URI, contentValues,
                AnswerDbSchema.Columns.TASK_ID + "=?", new String[]{String.valueOf(taskId)});
    }

    public static void removeAnswersByTaskId(Activity activity, int taskId) {
        activity.getContentResolver().delete(AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Columns.TASK_ID + "=?", new String[]{String.valueOf(taskId)});
    }

}
