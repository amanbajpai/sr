package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.util.SparseArray;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.Table;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TasksBL {
    public TasksBL() {

    }

    /**
     * 1. Get data from DB
     * 2. Update distance
     */
    public static void recalculateTasksDistance(Location myLocation) {
        Cursor cursor = getTasksFromDB();
        calculateTaskDistance(myLocation, cursor);
    }

    private static Cursor getTasksFromDB() {
        ContentResolver resolver = App.getInstance().getContentResolver();
        return resolver.query(TaskDbSchema.CONTENT_URI, TaskDbSchema.Query.All.PROJECTION,
                null, null, TaskDbSchema.SORT_ORDER_DESC);
    }

    public static void getNotMyTasksFromDBbyRadius(AsyncQueryHandler handler, int taskRadius, boolean withHiddenTasks) {
        String withHiddenTaskWhere = withHiddenTasks ? "" : " and " + TaskDbSchema.Columns.IS_HIDE + "=0";

        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.DISTANCE + "<=? and " + Table.TASK.getName()
                        + "." + TaskDbSchema.Columns.IS_MY.getName() + "= ?" + withHiddenTaskWhere,
                new String[]{String.valueOf(taskRadius), String.valueOf(0)}, TaskDbSchema.SORT_ORDER_DESC
        );
    }

    public static void getAllNotMyTasksFromDB(AsyncQueryHandler handler, boolean showHiddenTasks, Integer radius) {
        String withHiddenTaskWhere = showHiddenTasks ? "" : " and " + TaskDbSchema.Columns.IS_HIDE + "=0";

        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, Table.TASK.getName()
                        + "." + TaskDbSchema.Columns.IS_MY.getName() + "= ?" + withHiddenTaskWhere,
                new String[]{String.valueOf(0)}, TaskDbSchema.SORT_ORDER_DESC
        );
    }


    public static void getTaskFromDBbyID(AsyncQueryHandler handler, Integer taskId) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.ID + "=?",
                new String[]{String.valueOf(taskId)}, TaskDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    /**
     * Get tasks for one Wave
     *
     * @param handler - handler for request to DB
     * @param waveId  - waveId
     */
    public static void getTasksFromDBbyWaveId(AsyncQueryHandler handler, int waveId) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.WAVE_ID + "=?",
                new String[]{String.valueOf(waveId)},
                TaskDbSchema.SORT_ORDER_DESC);
    }

    public static void getNotMyTasksFromDBbyWaveId(AsyncQueryHandler handler, int waveId, boolean withHiddenTasks) {
        String withHiddenTaskWhere = withHiddenTasks ? "" : " and " + TaskDbSchema.Columns.IS_HIDE + "=0";

        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.WAVE_ID + "=? and "
                        + TaskDbSchema.Columns.IS_MY + "=?" + withHiddenTaskWhere,
                new String[]{String.valueOf(waveId), String.valueOf(0)},
                TaskDbSchema.SORT_ORDER_DESC
        );
    }

    public static void getMyTasksFromDB(AsyncQueryHandler handler) {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1 and (("
                        + TaskDbSchema.Columns.STATUS_ID + " = " + Task.TaskStatusId.reDoTask.getStatusId() + " and ("
                        + TaskDbSchema.Columns.LONG_REDO_DATE_TIME + " + " + TaskDbSchema.Columns
                        .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK + ") > " + currentTime + ") or ("
                        + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.reDoTask.getStatusId() + " and ("
                        + TaskDbSchema.Columns.LONG_CLAIM_DATE_TIME + " + " + TaskDbSchema.Columns
                        .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK + ") > " + currentTime + ") or ("
                        + TaskDbSchema.Columns.STATUS_ID + " == " + Task.TaskStatusId.validation.getStatusId()
                        + "))",
                null, TaskDbSchema.SORT_ORDER_DESC_MY_TASKS_LIST
        );
    }

    public static void getMyTasksForMapFromDB(AsyncQueryHandler handler) {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1 and (("
                        + TaskDbSchema.Columns.STATUS_ID + " = " + Task.TaskStatusId.reDoTask.getStatusId() + " and ("
                        + TaskDbSchema.Columns.LONG_REDO_DATE_TIME + " + " + TaskDbSchema.Columns
                        .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK + ") > " + currentTime + ") " +
                        " or (" + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.reDoTask.getStatusId() +
                        " and ("
                        + TaskDbSchema.Columns.LONG_CLAIM_DATE_TIME + " + " + TaskDbSchema.Columns
                        .LONG_EXPIRE_TIMEOUT_FOR_CLAIMED_TASK + ") > " + currentTime + ") or ("
                        + TaskDbSchema.Columns.STATUS_ID + " == " + Task.TaskStatusId.validation.getStatusId()
                        + ")) and "
                        + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.completed.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.validated.getStatusId()
                        + " and " + TaskDbSchema.Columns.STATUS_ID + " <> " + Task.TaskStatusId.rejected.getStatusId(),
                null, TaskDbSchema.SORT_ORDER_DESC_MY_TASKS_LIST
        );
    }

    public static void getTaskToRemindFromDB(AsyncQueryHandler handler, int coockie, long fromTime, long tillTime) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, coockie, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1 and "
                        + TaskDbSchema.Columns.END_DATE_TIME
                        + ">" + fromTime + " and " + TaskDbSchema.Columns.END_DATE_TIME
                        + "<" + tillTime + " and ("
                        + TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.claimed.getStatusId() + " or "
                        + TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.started.getStatusId() + " or "
                        + TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.reDoTask.getStatusId() + " or "
                        + TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.scheduled.getStatusId() + ") ",
                null, TaskDbSchema.SORT_ORDER_ASC_LIMIT_1
        );
    }

    public static void setHideTaskOnMapByID(AsyncQueryHandler handler, Integer taskId, Boolean isHide) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskDbSchema.Columns.IS_HIDE.getName(), isHide);

        handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE, null, TaskDbSchema.CONTENT_URI, contentValues,
                TaskDbSchema.Columns.ID + "=?", new String[]{String.valueOf(taskId)});
    }

    public static void setHideAllProjectTasksOnMapByID(AsyncQueryHandler handler, Integer waveId, Boolean isHide) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskDbSchema.Columns.IS_HIDE.getName(), isHide);

        handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE, null, TaskDbSchema.CONTENT_URI, contentValues,
                TaskDbSchema.Columns.WAVE_ID + "=?", new String[]{String.valueOf(waveId)});
    }

    public static void updateTask(AsyncQueryHandler handler, Task task) {
        handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE, null, TaskDbSchema.CONTENT_URI,
                task.toContentValues(), TaskDbSchema.Columns.ID + "=?", new String[]{String.valueOf(task.getId())});
    }

    /**
     * Update task statusId
     *
     * @param taskId   - current task id
     * @param statusId - new task status id
     */
    public static void updateTaskStatusId(Integer taskId, Integer statusId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskDbSchema.Columns.STATUS_ID.getName(), statusId);

        String where = TaskDbSchema.Columns.ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(taskId)};

        App.getInstance().getContentResolver().update(TaskDbSchema.CONTENT_URI, contentValues, where, whereArgs);
    }

    /**
     * @param currentLocation - user current location
     * @param cursor          - Cursor with data set from DB
     */

    private static void calculateTaskDistance(final Location currentLocation, Cursor cursor) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Location taskLocation = new Location(LocationManager.NETWORK_PROVIDER);
        ContentValues contentValues = new ContentValues();

        List<Task> tasks = convertCursorToTasksList(cursor);

        if (currentLocation != null) {
            for (Task task : tasks) {
                if (task.getLatitude() != null && task.getLongitude() != null) {
                    taskLocation.setLatitude(task.getLatitude());
                    taskLocation.setLongitude(task.getLongitude());

                    contentValues.put(TaskDbSchema.Columns.DISTANCE.getName(), currentLocation.distanceTo(taskLocation));
                } else {
                    contentValues.put(TaskDbSchema.Columns.DISTANCE.getName(), 0f);
                }

                String where = TaskDbSchema.Columns.ID + "=?";
                String[] whereArgs = new String[]{String.valueOf(task.getId())};

                resolver.update(TaskDbSchema.CONTENT_URI, contentValues, where, whereArgs);
            }
        }
    }

    /**
     * Convert cursor to Task list
     *
     * @param cursor - all fields cursor
     * @return ArrayList<Task>
     */
    public static List<Task> convertCursorToTasksList(Cursor cursor) {
        List<Task> result = new ArrayList<Task>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result.add(Task.fromCursor(cursor));
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
            while (cursor.moveToNext()) {
                result = Task.fromCursor(cursor);
            }
            cursor.close();
        }

        return result;
    }

    public static Task convertCursorToTaskOrNull(Cursor cursor) {
        Task result = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                result = Task.fromCursor(cursor);
            }
            cursor.close();
        }

        return result;
    }

    public static Task.TaskStatusId getTaskStatusType(int statusId) {
        Task.TaskStatusId result = Task.TaskStatusId.none;
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
                TaskDbSchema.Columns.STATUS_ID + "=" + Task.TaskStatusId.scheduled.getStatusId(),
                null, null);

        //Get tasks with 'scheduled' status id
        SparseArray<ContentValues> scheduledContentValuesMap = new SparseArray<ContentValues>();
        if (scheduledTasksCursor != null) {
            while (scheduledTasksCursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TaskDbSchema.Columns.STATUS_ID.getName(),
                        Task.TaskStatusId.scheduled.getStatusId());

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
}
