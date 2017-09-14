package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.SparseArray;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.Table;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;

public class TasksBL {
    private TasksBL() {
    }

    public static Cursor getTaskCursorFromDBbyID(Integer taskId, Integer missionId) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor c;
        if (missionId == null || missionId == 0) {
            c = resolver.query(TaskDbSchema.CONTENT_URI, TaskDbSchema.Query.All.PROJECTION,
                    TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + " IS NULL",
                    new String[]{String.valueOf(taskId)},
                    TaskDbSchema.SORT_ORDER_DESC_LIMIT_1);
        } else {
            c = resolver.query(TaskDbSchema.CONTENT_URI, TaskDbSchema.Query.All.PROJECTION,
                    TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + "=?",
                    new String[]{String.valueOf(taskId), String.valueOf(missionId)},
                    TaskDbSchema.SORT_ORDER_DESC_LIMIT_1);
        }
        return c;
    }

    public static Observable<List<Task>> getTaskFromDBbyIdObservable(Integer taskId, Integer missionId) {
        return Observable.fromCallable(() -> convertCursorToTasksList(getTaskCursorFromDBbyID(taskId, missionId)));
    }

    public static Observable<Task> getSingleTaskFromDBbyIdObservable(Integer taskId, Integer missionId) {
        return Observable.fromCallable(() -> convertCursorToTaskOrNull(getTaskCursorFromDBbyID(taskId, missionId)));
    }

    private static List<Task> getAllNotMyTasksFromDBSync(boolean showHiddenTasks) {
        String withHiddenTaskWhere = showHiddenTasks ? "" : " and " + TaskDbSchema.Columns.IS_HIDE + "=0";
        Cursor c = App.getInstance().getContentResolver().query(TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, Table.TASK.getName()
                        + "." + TaskDbSchema.Columns.IS_MY.getName() + "= ?" + withHiddenTaskWhere,
                new String[]{String.valueOf(0)}, TaskDbSchema.SORT_ORDER_DESC);
        return TasksBL.convertCursorToTasksList(c);
    }

    public static Observable<List<Task>> getAllNotMyTasksObservable(boolean showHiddenTasks) {
        return Observable.fromCallable(() -> getAllNotMyTasksFromDBSync(showHiddenTasks));
    }

    private static List<Task> getNotMyTasksFromDBbyWaveId(int waveId, boolean withHiddenTasks) {
        String withHiddenTaskWhere = withHiddenTasks ? "" : " and " + TaskDbSchema.Columns.IS_HIDE + "=0";
        Cursor c = App.getInstance().getContentResolver().query(TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.WAVE_ID + "=? and "
                        + TaskDbSchema.Columns.IS_MY + "=?" + withHiddenTaskWhere,
                new String[]{String.valueOf(waveId), String.valueOf(0)},
                TaskDbSchema.SORT_ORDER_DESC
        );
        return convertCursorToTasksList(c);
    }

    private static List<Task> getMyTasksForMapFromDB() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Cursor c = App.getInstance().getContentResolver().query(TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1 "
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.WITHDRAW.getStatusId()
                        + " and " + TaskDbSchema.Columns.LONG_EXPIRE_DATE_TIME + " > " + currentTime
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.COMPLETED.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.VALIDATED.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.IN_PAYMENT_PROCESS.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.PAID.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.REJECTED.getStatusId(),
                null, TaskDbSchema.SORT_ORDER_DESC_MY_TASKS_LIST);
        return convertCursorToTasksList(c);
    }

    public static Observable<List<Task>> getMyTasksForMapObservable() {
        return Observable.fromCallable(TasksBL::getMyTasksForMapFromDB);
    }

    public static Observable<List<Task>> getNotMyTasksObservable(int waveId, boolean isHidden) {
        return Observable.fromCallable(() -> getNotMyTasksFromDBbyWaveId(waveId, isHidden));
    }

    private static List<Task> getMyTasksFromDB() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Cursor c =
                App.getInstance().getContentResolver()
                        .query(TaskDbSchema.CONTENT_URI,
                                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1 "
                                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.WITHDRAW.getStatusId()
                                        + " and (" + TaskDbSchema.Columns.LONG_EXPIRE_DATE_TIME + " > " + currentTime
                                        + " or " + TaskDbSchema.Columns.STATUS_ID + " == " + Task.TaskStatusId.VALIDATION.getStatusId()
                                        + " or " + TaskDbSchema.Columns.STATUS_ID + " == " + Task.TaskStatusId.VALIDATED.getStatusId()
                                        + " or " + TaskDbSchema.Columns.STATUS_ID + " == " + Task.TaskStatusId.REJECTED.getStatusId()
                                        + ")",
                                null, TaskDbSchema.SORT_ORDER_DESC_MY_TASKS_LIST);
        return convertCursorToTasksList(c);
    }

    public static Observable<List<Task>> getMyTasksObservable() {
        return Observable.fromCallable(TasksBL::getMyTasksFromDB);
    }

    private static Integer setHideTaskOnMapByID(Integer taskId, Integer missionId, Boolean isHide) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskDbSchema.Columns.IS_HIDE.getName(), isHide);
        ContentResolver cr = App.getInstance().getContentResolver();
        if (missionId == null || missionId == 0)
            return cr.update(TaskDbSchema.CONTENT_URI, contentValues,
                    TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + " IS NULL",
                    new String[]{String.valueOf(taskId)});
        else
            return cr.update(TaskDbSchema.CONTENT_URI, contentValues,
                    TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + "=?",
                    new String[]{String.valueOf(taskId), String.valueOf(missionId)});
    }

    public static Observable<Integer> getHideTaskOnMapByIdObservable(Integer taskId, Integer missionId, Boolean isHide) {
        return Observable.fromCallable(() -> setHideTaskOnMapByID(taskId, missionId, isHide));
    }

    private static Integer updateTask(Task task, Integer missionId) {
        ContentResolver cr = App.getInstance().getContentResolver();
        if (missionId == null || missionId == 0) {
            return cr.update(TaskDbSchema.CONTENT_URI,
                    task.toContentValues(), TaskDbSchema.Columns.ID + "=? and " +
                            TaskDbSchema.Columns.MISSION_ID + " IS NULL", new String[]{String.valueOf(task.getId())});
        } else {
            return cr.update(TaskDbSchema.CONTENT_URI,
                    task.toContentValues(), TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + "=?",
                    new String[]{String.valueOf(task.getId()), String.valueOf(missionId)});
        }
    }

    public static Observable<Integer> getUpdateTaskObservable(Task task, Integer missionId) {
        return Observable.fromCallable(() -> updateTask(task, missionId));
    }

    // ----------------------- !!!! --------------------//

    public static void getTaskFromDBbyID(AsyncQueryHandler handler, Integer taskId, Integer missionId) {
        if (missionId == null || missionId == 0) {
            handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                    TaskDbSchema.Query.All.PROJECTION,
                    TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + " IS NULL",
                    new String[]{String.valueOf(taskId)},
                    TaskDbSchema.SORT_ORDER_DESC_LIMIT_1);
        } else {
            handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                    TaskDbSchema.Query.All.PROJECTION,
                    TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + "=?",
                    new String[]{String.valueOf(taskId), String.valueOf(missionId)},
                    TaskDbSchema.SORT_ORDER_DESC_LIMIT_1);
        }
    }

    public static void getTasksFromDB(AsyncQueryHandler handler) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, null, null, TaskDbSchema.SORT_ORDER_DESC);
    }

    public static void getMyTasksForMapFromDB(AsyncQueryHandler handler) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1 "
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.WITHDRAW.getStatusId()
                        + " and " + TaskDbSchema.Columns.LONG_EXPIRE_DATE_TIME + " > " + currentTime
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.COMPLETED.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.VALIDATED.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.IN_PAYMENT_PROCESS.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.PAID.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.REJECTED.getStatusId(),
                null, TaskDbSchema.SORT_ORDER_DESC_MY_TASKS_LIST
        );
    }

    public static void getMyTasksForMainMenuFromDB(AsyncQueryHandler handler) {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1 "
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.WITHDRAW.getStatusId()
                        + " and " + TaskDbSchema.Columns.LONG_EXPIRE_DATE_TIME + " > " + currentTime
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.COMPLETED.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.VALIDATED.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.VALIDATION.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.IN_PAYMENT_PROCESS.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.PAID.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.REJECTED.getStatusId(),
                null, TaskDbSchema.SORT_ORDER_DESC_MY_TASKS_LIST
        );
    }

    public static void getTaskToRemindFromDB(AsyncQueryHandler handler, int coockie, long fromTime, long tillTime) {
        String betweenClaimedTime = "("
                + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.RE_DO_TASK.getStatusId() + " and ("
                + TaskDbSchema.Columns.LONG_CLAIM_DATE_TIME + " + " + TaskDbSchema.Columns
                .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK + ") >= " + fromTime + " and ("
                + TaskDbSchema.Columns.LONG_CLAIM_DATE_TIME + " + " + TaskDbSchema.Columns
                .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK + ") < " + tillTime + ") ";

        String betweenReDoTime = "("
                + TaskDbSchema.Columns.STATUS_ID + " = " + Task.TaskStatusId.RE_DO_TASK.getStatusId() + " and ("
                + TaskDbSchema.Columns.LONG_REDO_DATE_TIME + " + " + TaskDbSchema.Columns
                .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK + ") >= " + fromTime + " and ("
                + TaskDbSchema.Columns.LONG_REDO_DATE_TIME + " + " + TaskDbSchema.Columns
                .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK + ") < " + tillTime + ") ";

        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, coockie, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1 and ("
                        + betweenClaimedTime + " or "
                        + betweenReDoTime + ") and "
                        + " ("
                        + TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.CLAIMED.getStatusId() + " or "
                        + TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.STARTED.getStatusId() + " or "
                        + TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.RE_DO_TASK.getStatusId() + " or "
                        + TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.SCHEDULED.getStatusId() + ") ",
                null, TaskDbSchema.SORT_ORDER_ASC_LIMIT_1
        );
    }

    public static void setHideAllProjectTasksOnMapByID(AsyncQueryHandler handler, Integer waveId, Boolean isHide) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskDbSchema.Columns.IS_HIDE.getName(), isHide);

        handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE, null, TaskDbSchema.CONTENT_URI, contentValues,
                TaskDbSchema.Columns.WAVE_ID + "=?", new String[]{String.valueOf(waveId)});
    }

    public static void updateTask(AsyncQueryHandler handler, Task task) {
        Integer missionId = task.getMissionId();
        if (missionId == null || missionId == 0) {
            handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE, null, TaskDbSchema.CONTENT_URI,
                    task.toContentValues(), TaskDbSchema.Columns.ID + "=? and " +
                            TaskDbSchema.Columns.MISSION_ID + " IS NULL", new String[]{String.valueOf(task.getId())});
        } else {
            handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE, null, TaskDbSchema.CONTENT_URI,
                    task.toContentValues(), TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + "=?",
                    new String[]{String.valueOf(task.getId()), String.valueOf(missionId)});
        }
    }

    /**
     * Update task
     *
     * @param task - task to update
     */
    public static void updateTaskSync(Task task) {
        String where = TaskDbSchema.Columns.ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(task.getId())};

        App.getInstance().getContentResolver().update(TaskDbSchema.CONTENT_URI,
                task.toContentValues(), where, whereArgs);
    }

    /**
     * Update task statusId
     *
     * @param taskId   - current task id
     * @param statusId - new task status id
     */
    public static void updateTaskStatusId(Integer taskId, Integer missionId, Integer statusId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskDbSchema.Columns.STATUS_ID.getName(), statusId);

        String where = TaskDbSchema.Columns.ID + "=? and " + TaskDbSchema.Columns.MISSION_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(taskId), String.valueOf(missionId)};

        App.getInstance().getContentResolver().update(TaskDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    /**
     * 1. Get data from DB
     * 2. Update distance
     *
     * @param currentLocation - user current location
     * @param cursor          - Cursor with data set from DB
     */

    public static void calculateTaskDistance(AsyncQueryHandler handler, final Location currentLocation, Cursor cursor) {
        new RecalculateDistanceAsyncTask(handler, cursor).execute(currentLocation);
    }


    public static class RecalculateDistanceAsyncTask extends AsyncTask<Location, Void, Void> {
        private AsyncQueryHandler handler;
        private Cursor cursor;

        public RecalculateDistanceAsyncTask(AsyncQueryHandler handler, Cursor cursor) {
            this.handler = handler;
            this.cursor = cursor;
        }

        @Override
        protected Void doInBackground(Location... params) {
            Location currentLocation = params[0];
            Location taskLocation = new Location(LocationManager.NETWORK_PROVIDER);

            final List<Task> tasks = TasksBL.convertCursorToTasksList(cursor);

            if (currentLocation != null && tasks != null) {
                final int tasksCount = tasks.size();
                for (int i = 0; i < tasksCount; i++) {
                    Task task = tasks.get(i);
                    ContentValues contentValues = new ContentValues();
                    if (task.getLatitude() != null && task.getLongitude() != null) {
                        taskLocation.setLatitude(task.getLatitude());
                        taskLocation.setLongitude(task.getLongitude());

                        float distance = currentLocation.distanceTo(taskLocation);
                        /*L.e("Distance", "Task id = " + task.getId());
                        L.e("Distance", "Distance = " + distance);*/
                        contentValues.put(TaskDbSchema.Columns.DISTANCE.getName(), distance);
                    } else {
                        contentValues.put(TaskDbSchema.Columns.DISTANCE.getName(), 0f);
                    }

                    String where = TaskDbSchema.Columns.ID + "=?";
                    String[] whereArgs = new String[]{String.valueOf(task.getId())};

                    handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE,
                            i == tasksCount - 1, TaskDbSchema.CONTENT_URI, contentValues, where, whereArgs);
                }
            }
            return null;
        }
    }

    /**
     * Calculates distance to the task according to specific Location
     *
     * @param task            task to get location for
     * @param currentLocation location to distance calculation
     * @return distance to task or 0 if location of the task is not specified
     */
    public static float getDistanceForTask(Task task, Location currentLocation) {
        Location taskLocation = new Location(LocationManager.NETWORK_PROVIDER);
        if (task.getLatitude() != null && task.getLongitude() != null && currentLocation != null) {
            taskLocation.setLatitude(task.getLatitude());
            taskLocation.setLongitude(task.getLongitude());
            return currentLocation.distanceTo(taskLocation);
        } else {
            return 0f;
        }
    }

    /**
     * Convert cursor to Task list
     *
     * @param cursor - all fields cursor
     * @return ArrayList<Task>
     */
    public static List<Task> convertCursorToTasksList(Cursor cursor) {
        List<Task> result = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                result.add(Task.fromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return result;
    }

    /**
     * Convert cursor to Task
     *
     * @param cursor - all fields cursor
     * @return Task
     */
    public static Task convertCursorToTask(Cursor cursor) {
        Task result = new Task();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = Task.fromCursor(cursor);
            }
            cursor.close();
        }

        return result;
    }

    /**
     * Convert cursor to Tasks count
     *
     * @param cursor - all fields cursor
     * @return int
     */
    public static int convertCursorToTasksCount(Cursor cursor) {
        int result = 0;
        if (cursor != null) {
            result = cursor.getCount();
        }

        return result;
    }

    public static Task convertCursorToTaskOrNull(Cursor cursor) {
        Task result = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = Task.fromCursor(cursor);
            }
            cursor.close();
        }

        return result;
    }

    public static Task.TaskStatusId getTaskStatusType(int statusId) {
        Task.TaskStatusId result = Task.TaskStatusId.NONE;
        for (Task.TaskStatusId status : Task.TaskStatusId.values()) {
            if (status.getStatusId() == statusId) {
                result = status;
                break;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static SparseArray<ContentValues> getScheduledTaskHashMap(ContentResolver contentResolver) {
        String[] projection = {Table.TASK.getName() + "." + TaskDbSchema.Columns.ID.getName()};

        Cursor scheduledTasksCursor = contentResolver.query(TaskDbSchema.CONTENT_URI, projection,
                TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.SCHEDULED.getStatusId(),
                null, null);

        //Get tasks with 'scheduled' status id
        SparseArray<ContentValues> scheduledContentValuesMap = new SparseArray<ContentValues>();
        if (scheduledTasksCursor != null) {
            while (scheduledTasksCursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TaskDbSchema.Columns.STATUS_ID.getName(),
                        Task.TaskStatusId.SCHEDULED.getStatusId());

                scheduledContentValuesMap.put(scheduledTasksCursor.getInt(0), contentValues);
            }
            scheduledTasksCursor.close();
        }

        return scheduledContentValuesMap;
    }

    @SuppressWarnings("unchecked")
    public static SparseArray<ContentValues> getHiddenTaskHashMap(ContentResolver contentResolver) {
        String[] projection = {Table.TASK.getName() + "." + TaskDbSchema.Columns.ID.getName()};

        Cursor tasksCursor = contentResolver.query(TaskDbSchema.CONTENT_URI, projection,
                TaskDbSchema.Columns.IS_HIDE + "=1", null, null);

        SparseArray<ContentValues> contentValuesMap = new SparseArray<ContentValues>();
        if (tasksCursor != null) {
            while (tasksCursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TaskDbSchema.Columns.IS_HIDE.getName(), true);

                contentValuesMap.put(tasksCursor.getInt(0), contentValues);
            }
            tasksCursor.close();
        }

        return contentValuesMap;
    }

    @SuppressWarnings("unchecked")
    public static SparseArray<ContentValues> getValidLocationTaskHashMap(ContentResolver contentResolver) {
        String[] projection = {Table.TASK.getName() + "." + TaskDbSchema.Columns.ID.getName(),
                Table.TASK.getName() + "." + TaskDbSchema.Columns.LATITUDE_TO_VALIDATION.getName(),
                Table.TASK.getName() + "." + TaskDbSchema.Columns.LONGITUDE_TO_VALIDATION.getName()};

        Cursor tasksCursor = contentResolver.query(TaskDbSchema.CONTENT_URI, projection,
                TaskDbSchema.Columns.LATITUDE_TO_VALIDATION + " IS NOT NULL and "
                        + TaskDbSchema.Columns.LONGITUDE_TO_VALIDATION + " IS NOT NULL", null, null
        );

        SparseArray<ContentValues> contentValuesMap = new SparseArray<ContentValues>();
        if (tasksCursor != null) {
            while (tasksCursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TaskDbSchema.Columns.LATITUDE_TO_VALIDATION.getName(), tasksCursor.getDouble(1));
                contentValues.put(TaskDbSchema.Columns.LONGITUDE_TO_VALIDATION.getName(), tasksCursor.getDouble(2));

                contentValuesMap.put(tasksCursor.getInt(0), contentValues);
            }
            tasksCursor.close();
        }

        return contentValuesMap;
    }

    public static void updateTasksByContentValues(ContentResolver contentResolver,
                                                  SparseArray<ContentValues> contentValuesMap) {
        for (int i = 0; i < contentValuesMap.size(); i++) {
            Integer taskId = contentValuesMap.keyAt(i);
            ContentValues contentValues = contentValuesMap.get(taskId);

            contentResolver.update(TaskDbSchema.CONTENT_URI, contentValues,
                    TaskDbSchema.Columns.ID + "=?", new String[]{String.valueOf(taskId)});
        }
    }

    public static void removeAllMyTask(ContentResolver contentResolver) {
        contentResolver.delete(TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Columns.IS_MY + "=?", new String[]{String.valueOf(1)});
    }

    public static void removeNotMyTask(ContentResolver contentResolver) {
        contentResolver.delete(TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Columns.IS_MY + "=?", new String[]{String.valueOf(0)});
    }

    public static void removeTask(ContentResolver contentResolver, int taskId) {
        contentResolver.delete(TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Columns.ID + "=?", new String[]{String.valueOf(taskId)});
    }

    public static void removeTasksByWaveId(ContentResolver contentResolver, int waveId) {
        contentResolver.delete(TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Columns.WAVE_ID + "=?", new String[]{String.valueOf(waveId)});
    }

    public static void removeAllTasksFromDB(Context context) {
        context.getContentResolver().delete(TaskDbSchema.CONTENT_URI, null, null);
    }

    public static boolean isPreClaimTask(Task task) {
        return task.getLongStartDateTime() > Calendar.getInstance().getTimeInMillis();
    }
}
