package com.ros.smartrocket.db.bl;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.Table;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Category;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class AnswersBL {

    private static Integer removeAnswersByTaskId(int taskId) {
        return App.getInstance().getContentResolver()
                .delete(AnswerDbSchema.CONTENT_URI,
                        AnswerDbSchema.Columns.TASK_ID + "=?",
                        new String[]{String.valueOf(taskId)});
    }

    public static Observable<Integer> getRemoveAnswersByTaskIdObservable(int taskId) {
        return Observable.fromCallable(() -> removeAnswersByTaskId(taskId));
    }

    private static Cursor getAnswersListFromDB(Question question, Product product) {
        return App.getInstance().getContentResolver().query(
                AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Query.PROJECTION,
                AnswerDbSchema.Columns.QUESTION_ID + "=? and "
                        + AnswerDbSchema.Columns.TASK_ID + "=? and "
                        + AnswerDbSchema.Columns.MISSION_ID + "=? and "
                        + AnswerDbSchema.Columns.PRODUCT_ID + "=?",
                new String[]{String.valueOf(question.getId()),
                        String.valueOf(question.getTaskId()),
                        String.valueOf(question.getMissionId()),
                        String.valueOf(product.getId())},
                AnswerDbSchema.SORT_ORDER_ASC);
    }

    private static Cursor getAnswersListFromDB(Question question) {
        return App.getInstance().getContentResolver().query(
                AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Query.PROJECTION,
                AnswerDbSchema.Columns.QUESTION_ID + "=? and "
                        + AnswerDbSchema.Columns.TASK_ID + "=? and "
                        + AnswerDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(question.getId()),
                        String.valueOf(question.getTaskId()),
                        String.valueOf(question.getMissionId())},
                AnswerDbSchema.SORT_ORDER_ASC);
    }

    public static Observable<List<Answer>> getAnswersListFromDBObservable(Question question, Product product) {
        if (product != null && product.getId() != null)
            return Observable.fromCallable(() -> convertCursorToAnswerList(getAnswersListFromDB(question, product)));
        else
            return Observable.fromCallable(() -> convertCursorToAnswerList(getAnswersListFromDB(question)));
    }

    public static long insert(Answer answer) {
        Uri uri = App.getInstance().getContentResolver().insert(AnswerDbSchema.CONTENT_URI, answer.toContentValues());
        return ContentUris.parseId(uri);
    }

    private static int updateAnswersInDB(List<Answer> answers) {
        int count = 0;
        for (Answer answer : answers) {
            count += App.getInstance()
                    .getContentResolver()
                    .update(AnswerDbSchema.CONTENT_URI,
                            answer.toContentValues(),
                            AnswerDbSchema.Columns._ID + "=?",
                            new String[]{String.valueOf(answer.get_id())});
        }
        return count;
    }

    public static Completable getUpdateAnswersInDBObservable(List<Answer> answers) {
        return Completable.fromCallable(() -> updateAnswersInDB(answers));
    }

    private static int deleteAnswerFromDB(Answer answer) {
        return App.getInstance().getContentResolver().delete(AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Columns._ID + "=?", new String[]{String.valueOf(answer.get_id())});
    }

    public static Completable getDeleteAnswerFromDBObservable(Answer answer) {
        return Completable.fromCallable(() -> deleteAnswerFromDB(answer));
    }

    // ------------------ !!!! ----------------- //

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
     * @param handler - Handler for getting response from DB
     */
    public static void getSubQuestionsAnswersListFromDB(AsyncQueryHandler handler, Integer taskId, Integer missionId, Category[] categories) {

        StringBuilder where = new StringBuilder().append(AnswerDbSchema.Columns.TASK_ID + "=? and ")
                .append(AnswerDbSchema.Columns.MISSION_ID + "=? and ");

        ArrayList<Product> products = new ArrayList<>();
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(taskId));
        args.add(String.valueOf(missionId));

        for (int i = 0; i < categories.length; i++) {
            Product[] tempProducts = categories[i].getProducts();
            for (int j = 0; j < tempProducts.length; j++) {
                products.add(tempProducts[j]);
            }
        }

        for (int i = 0; i < products.size(); i++) {
            where.append(AnswerDbSchema.Columns.PRODUCT_ID + " =? ");
            if (i != products.size() - 1) {
                where.append(" or ");
            }

            args.add(String.valueOf(products.get(i).getId()));
        }


//        handler.startQuery(
//                AnswerDbSchema.Query.TOKEN_QUERY,
//                null,
//                AnswerDbSchema.CONTENT_URI,
//                AnswerDbSchema.Query.PROJECTION,
//                AnswerDbSchema.Columns.TASK_ID + "=? and "
//                        + AnswerDbSchema.Columns.MISSION_ID + "=? and "
//                        + AnswerDbSchema.Columns.PRODUCT_ID + " =?",
//                new String[]{String.valueOf(taskId), String.valueOf(missionId), "1,2,3"},
//                AnswerDbSchema.SORT_ORDER_ASC);

        handler.startQuery(
                AnswerDbSchema.Query.TOKEN_QUERY,
                null,
                AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Query.PROJECTION,
                where.toString(),
                args.toArray(new String[args.size()]),
                AnswerDbSchema.SORT_ORDER_ASC);
    }

    /**
     * Make request for getting Answer list
     *
     * @param handler - Handler for getting response from DB
     */

    public static void getSubQuestionsAnswersListFromDB(AsyncQueryHandler handler, Integer taskId, Integer missionId, Question[] questions) {

        StringBuilder where = new StringBuilder().append(AnswerDbSchema.Columns.TASK_ID + "=? and ")
                .append(AnswerDbSchema.Columns.MISSION_ID + "=? and (");

        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(taskId));
        args.add(String.valueOf(missionId));

        for (int i = 0; i < questions.length; i++) {
            where.append(AnswerDbSchema.Columns.QUESTION_ID + " =? ");
            if (i != questions.length - 1) {
                where.append(" or ");
            }

            args.add(String.valueOf(questions[i].getId()));
        }

//        where.append(AnswerDbSchema.Columns.QUESTION_ID + "=? ");
//        args.add(String.valueOf(questions[0].getId()));

        where.append(" ) and ").append(AnswerDbSchema.Columns.CHECKED + "=? ");
        args.add(String.valueOf(1));


//        handler.startQuery(
//                AnswerDbSchema.Query.TOKEN_QUERY,
//                null,
//                AnswerDbSchema.CONTENT_URI,
//                AnswerDbSchema.Query.PROJECTION,
//                AnswerDbSchema.Columns.TASK_ID + "=? and "
//                        + AnswerDbSchema.Columns.MISSION_ID + "=? and "
//                        + AnswerDbSchema.Columns.PRODUCT_ID + " =?",
//                new String[]{String.valueOf(taskId), String.valueOf(missionId), "1,2,3"},
//                AnswerDbSchema.SORT_ORDER_ASC);

        handler.startQuery(
                AnswerDbSchema.Query.TOKEN_QUERY,
                null,
                AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Query.PROJECTION,
                where.toString(),
                args.toArray(new String[args.size()]),
                AnswerDbSchema.SORT_ORDER_ASC);
    }

    public static void updateAnswersToDB(AsyncQueryHandler handler, List<Answer> answers) {
        for (Answer answer : answers) {
            handler.startUpdate(AnswerDbSchema.Query.TOKEN_UPDATE, null, AnswerDbSchema.CONTENT_URI,
                    answer.toContentValues(), AnswerDbSchema.Columns._ID + "=?",
                    new String[]{String.valueOf(answer.get_id())});
        }
    }

    public static void updateQuitStatmentAnswer(Question question) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerDbSchema.Columns.CHECKED.getName(), true);
        contentValues.put(AnswerDbSchema.Columns.VALUE.getName(), 1);
        App.getInstance().getContentResolver().update(AnswerDbSchema.CONTENT_URI,
                contentValues, AnswerDbSchema.Columns.QUESTION_ID + "=? and " +
                        AnswerDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(question.getId()), String.valueOf(question.getTaskId()), String.valueOf(question.getMissionId())});
    }

    public static void clearAnswersInDB(Integer taskId, Integer missionId, Integer questionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerDbSchema.Columns.CHECKED.getName(), false);

        App.getInstance().getContentResolver().update(AnswerDbSchema.CONTENT_URI,
                contentValues, AnswerDbSchema.Columns.QUESTION_ID + "=? and " +
                        AnswerDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.MISSION_ID + "=?",
                new String[]{String.valueOf(questionId), String.valueOf(taskId), String.valueOf(missionId)});
        App.getInstance().getContentResolver().update(AnswerDbSchema.CONTENT_URI,
                contentValues, AnswerDbSchema.Columns.TASK_ID + "=? and " + AnswerDbSchema.Columns.MISSION_ID + "=? and "
                        + AnswerDbSchema.Columns.QUESTION_ID + " IN (Select + " + QuestionDbSchema.Columns.ID
                        + " From " + Table.QUESTION.getName()
                        + " Where " + QuestionDbSchema.Columns.PARENT_QUESTION_ID + "=?)",
                new String[]{String.valueOf(taskId), String.valueOf(missionId), String.valueOf(questionId)});
    }

    public static void clearSubAnswersInDB(Integer taskId, Integer missionId, Integer productId, Question[] questions) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerDbSchema.Columns.CHECKED.getName(), false);
        List<Question> subQuestions = getSubQuestions(questions);
        if (subQuestions != null && !subQuestions.isEmpty()) {
            App.getInstance().getContentResolver().update(AnswerDbSchema.CONTENT_URI,
                    contentValues, AnswerDbSchema.Columns.TASK_ID + "=? and "
                            + AnswerDbSchema.Columns.MISSION_ID + "=? and "
                            + AnswerDbSchema.Columns.PRODUCT_ID + "=? and "
                            + AnswerDbSchema.Columns.QUESTION_ID + " IN (" + getPlaceholder(subQuestions.size() - 1) + ")",
                    getSubQuestionParams(subQuestions, taskId, missionId, productId));
        }
    }

    private static List<Question> getSubQuestions(Question[] questions) {
        List<Question> subQuestions = new ArrayList<>();
        for (Question q : questions) {
            if (q.getType() != Question.QuestionType.MAIN_SUB_QUESTION.getTypeId()) {
                subQuestions.add(q);
            }
        }
        return subQuestions;
    }

    private static String[] getSubQuestionParams(List<Question> questions, Integer taskId, Integer missionId, Integer productId) {
        ArrayList<String> params = new ArrayList<>();
        params.add(String.valueOf(taskId));
        params.add(String.valueOf(missionId));
        params.add(String.valueOf(productId));
        for (Question q : questions) {
            params.add(String.valueOf(q.getId()));
        }
        return params.toArray(new String[params.size()]);
    }

    private static String getPlaceholder(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= count; i++) {
            sb.append("?");
            if (i != count) {
                sb.append(",");
            }
        }
        return sb.toString();
    }


    /**
     * Return file's list to upload by task id
     *
     * @param taskId      - current task id
     * @param endDateTime - task finish date
     * @return List<NotUploadedFile>
     */

    public static List<NotUploadedFile> getTaskFilesListToUpload(Integer taskId, Integer missionId, String taskName,
                                                                 long endDateTime) {
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
                        AnswerDbSchema.Columns.MISSION_ID + "=? and " +
                        AnswerDbSchema.Columns.VALUE + " IS NOT NULL and " +
                        AnswerDbSchema.Columns.VALUE + " !=? "

                /* and " + AnswerDbSchema.Columns.FILE_URI + " IS NULL"*/,
                new String[]{String.valueOf(taskId), String.valueOf(1), String.valueOf(missionId), ""}, null);

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

                    TasksBL.updateTaskSync(task);
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
            L.e("AnswerBL", "Answer Latitude: " + answer.getLatitude());
            L.e("AnswerBL", "Answer Longitude: " + answer.getLongitude());
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

        L.e("AnswerBL", "Result Latitude: " + lat * 180 / Math.PI);
        L.e("AnswerBL", "Result Longitude: " + lon * 180 / Math.PI);

        task.setLatitudeToValidation(lat * 180 / Math.PI);
        task.setLongitudeToValidation(lon * 180 / Math.PI);

        TasksBL.updateTaskSync(task);
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

    private static List<Answer> convertCursorToAnswerList(Cursor cursor) {
        List<Answer> result = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Answer.fromCursor(cursor));
            }
            cursor.close();
        }
        return result;
    }

    public static int getNextQuestionOrderId(Question question, List<Question> questions) {
        int orderId = 0;

        if (question != null && questions != null) {
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
                for (Question q : questions) {
                    if (orderId <= q.getOrderId()) {
                        orderId = q.getOrderId();
                        break;
                    }
                }
            }
        }
        return orderId;
    }

    public static void clearTaskUserAnswers(Activity activity, int taskId, int missionId) {
        activity.getContentResolver().delete(AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Columns.TASK_ID + "=? and "
                        + AnswerDbSchema.Columns.FILE_URI.getName() + " IS NOT NULL", new String[]{String.valueOf
                        (taskId)}
        );

        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerDbSchema.Columns.CHECKED.getName(), false);
        contentValues.putNull(AnswerDbSchema.Columns.FILE_URI.getName());

        activity.getContentResolver().update(AnswerDbSchema.CONTENT_URI, contentValues,
                AnswerDbSchema.Columns.TASK_ID + "=?", new String[]{String.valueOf(taskId)});
    }

    public static void removeAllAnswers(Context context) {
        context.getContentResolver().delete(AnswerDbSchema.CONTENT_URI, null, null);
    }

    @Nullable
    public static Answer getAnswer(Answer[] answers, Integer id, String value) {
        for (Answer answer : answers) {
            if (answer.getProductId().equals(id) && answer.getValue().equals(value)) {
                return answer;
            }
        }

        return null;
    }
}
