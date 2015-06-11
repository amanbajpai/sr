package com.ros.smartrocket.bl;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AnswersBL {

    private AnswersBL() {

    }

    /**
     * Update missionId
     *
     * @param taskId    - current taskId
     * @param missionId - missionId to set
     */
    public static void setMissionId(Integer taskId, Integer missionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerDbSchema.Columns.MISSION_ID.getName(), missionId);

        String where = AnswerDbSchema.Columns.TASK_ID + "=? and (" +
                AnswerDbSchema.Columns.MISSION_ID + "=? or " + AnswerDbSchema.Columns.MISSION_ID + " IS NULL )";
        String[] whereArgs = new String[]{String.valueOf(taskId), String.valueOf(0)};

        App.getInstance().getContentResolver().update(AnswerDbSchema.CONTENT_URI, contentValues, where, whereArgs);
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
     * Make request for update Answers
     *
     * @param handler - Handler for getting response from DB
     * @param answers - answers array
     */

    public static void updateAnswersToDB(AsyncQueryHandler handler, Answer[] answers) {
        for (Answer answer : answers) {
            handler.startUpdate(AnswerDbSchema.Query.TOKEN_UPDATE, null, AnswerDbSchema.CONTENT_URI,
                    answer.toContentValues(), AnswerDbSchema.Columns._ID + "=?",
                    new String[]{String.valueOf(answer.get_id())});
        }
    }

    public static void clearAnswersInDB(Integer taskId, Integer missionId, Integer questionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerDbSchema.Columns.CHECKED.getName(), false);

        App.getInstance().getContentResolver().update(AnswerDbSchema.CONTENT_URI,
                contentValues, AnswerDbSchema.Columns.QUESTION_ID + "=? and " +
                        AnswerDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(questionId), String.valueOf(taskId), String.valueOf(missionId)});

    }

    /**
     * Make request for delete Answer
     *
     * @param handler - Handler for getting response from DB
     * @param answer  - answer to delete
     */

    public static void deleteAnswerFromDB(AsyncQueryHandler handler, Answer answer) {
        handler.startDelete(AnswerDbSchema.Query.TOKEN_DELETE, null, AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Columns._ID + "=?", new String[]{String.valueOf(answer.get_id())});
    }

    /**
     * Return file's list to upload by task id
     *
     * @param taskId      - current task id
     * @param endDateTime - task finish date
     * @return List<NotUploadedFile>
     */

    public static List<NotUploadedFile> getTaskFilesListToUpload(Integer taskId, Integer missionId, String taskName, long endDateTime) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(AnswerDbSchema.CONTENT_URI, AnswerDbSchema.Query.PROJECTION,
                AnswerDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.CHECKED + "=? and " +
                        AnswerDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(taskId), String.valueOf(1), String.valueOf(missionId)}, null);

        Answer[] answers = convertCursorToAnswersArray(cursor);

        List<NotUploadedFile> notUploadedFiles = new ArrayList<NotUploadedFile>();
        for (Answer answer : answers) {
            if (!TextUtils.isEmpty(answer.getFileUri())) {
                NotUploadedFile fileToUpload = new NotUploadedFile();
                fileToUpload.setRandomId();
                fileToUpload.setTaskId(answer.getTaskId());
                fileToUpload.setMissionId(answer.getMissionId());
                fileToUpload.setTaskName(taskName);
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

    /**
     * Make request for getting size of files in MB
     *
     * @param filesToUpload - files to upload
     * @return Float
     */

    public static float getTaskFilesSizeMb(List<NotUploadedFile> filesToUpload) {
        long resultSizeInB = 0;
        for (NotUploadedFile fileToUpload : filesToUpload) {
            File file = new File(Uri.parse(fileToUpload.getFileUri()).getPath());
            if (file.exists()) {
                resultSizeInB = resultSizeInB + file.length();
            }
        }

        return resultSizeInB / (float) 1024;
    }

    /**
     * Return size of file as String in MB or KB
     *
     * @param size - file size
     * @return String
     */
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

    public static List<Answer> getAnswersListToSend(Integer taskId, Integer missionId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(AnswerDbSchema.CONTENT_URI, AnswerDbSchema.Query.PROJECTION,
                AnswerDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.CHECKED + "=? and " +
                        AnswerDbSchema.Columns.MISSION_ID + "=?"
                /* and " + AnswerDbSchema.Columns.FILE_URI + " IS NULL"*/,
                new String[]{String.valueOf(taskId), String.valueOf(1), String.valueOf(missionId)}, null);

        return convertCursorToAnswerList(cursor);
    }

    public static boolean isHasFile(List<Answer> answerListToSend) {
        boolean hasFile = false;
        for (Answer answer : answerListToSend) {
            if (!TextUtils.isEmpty(answer.getFileName()) && !TextUtils.isEmpty(answer.getValue())) {
                hasFile = true;
                break;
            }
        }

        return hasFile;
    }

    public static void saveValidationLocation(final Task task, final List<Answer> answerList, boolean hasFile) {
        if (hasFile) {
            savePhotoVideoAnswersAverageLocation(task, answerList);
        } else {
            MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
                @Override
                public void getLocationStart() {
                }

                @Override
                public void getLocationInProcess() {
                }

                @Override
                public void getLocationSuccess(Location location) {
                    task.setLatitudeToValidation(location.getLatitude());
                    task.setLongitudeToValidation(location.getLongitude());

                    TasksBL.updateTask(task);
                }

                @Override
                public void getLocationFail(String errorText) {
                    UIUtils.showSimpleToast(App.getInstance(), errorText);
                }
            });
        }
    }

    /**
     * Calculate average location for list of answers. And Save it to local DB
     *
     * @param task
     * @param answerList
     */
    public static void savePhotoVideoAnswersAverageLocation(final Task task, final List<Answer> answerList) {
        int photoVideoAnswerCount = 0;

        double x = 0;
        double y = 0;
        double z = 0;

        for (Answer answer : answerList) {
            if (answer.getLatitude() == 0 || answer.getLongitude() == 0) {
                continue;
            }
            photoVideoAnswerCount++;

            double lat = answer.getLatitude() * Math.PI / 180;
            double lon = answer.getLongitude() * Math.PI / 180;

            double tempX = Math.cos(lat) * Math.cos(lon);
            double tempY = Math.cos(lat) * Math.sin(lon);
            double tempZ = Math.sin(lat);

            x += tempX;
            y += tempY;
            z += tempZ;
        }

        x = x / photoVideoAnswerCount;
        y = y / photoVideoAnswerCount;
        z = z / photoVideoAnswerCount;

        double lon = Math.atan2(y, x);
        double hyp = Math.sqrt(x * x + y * y);
        double lat = Math.atan2(z, hyp);

        L.e("AnswerBL", "Latitude: " + lat * 180 / Math.PI);
        L.e("AnswerBL", "Longitude: " + lon * 180 / Math.PI);

        task.setLatitudeToValidation(lat * 180 / Math.PI);
        task.setLongitudeToValidation(lon * 180 / Math.PI);

        TasksBL.updateTask(task);
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
    public static List<Answer> convertCursorToAnswerList(Cursor cursor) {
        List<Answer> result = new ArrayList<Answer>();
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

        if (question != null) {
            if (question.getRouting() != null && question.getRouting() != 0) {
                orderId = question.getRouting();
            } else if (question.getAnswers() != null) {
                for (Answer answer : question.getAnswers()) {
                    if (answer != null && answer.getRouting() != null && answer.getChecked()) {
                        orderId = answer.getRouting();
                        break;
                    }
                }
            }

            if (orderId == 0) {
                orderId = question.getOrderId() + 1;
            }
        }
        return orderId;
    }

    public static void clearTaskUserAnswers(Activity activity, int taskId, int missionId) {
        activity.getContentResolver().delete(AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Columns.TASK_ID + "=? and "
                        + AnswerDbSchema.Columns.FILE_URI.getName() + " IS NOT NULL", new String[]{String.valueOf(taskId)}
        );

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

    public static void removeAllAnswers(Context context) {
        context.getContentResolver().delete(AnswerDbSchema.CONTENT_URI, null, null);
    }
}
