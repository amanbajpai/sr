package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;

import java.util.ArrayList;

/**
 * Created by bopr on 12/10/13.
 */
public class TasksBL {
    //private static final String TAG = TasksBL.class.getSimpleName();


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

    public static void getTasksFromDBbyRadius(AsyncQueryHandler handler, int taskRadius) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.DISTANCE + "<=?",
                new String[]{String.valueOf(taskRadius)}, TaskDbSchema.SORT_ORDER_DESC);
    }



    public static void getTaskFromDBbyID(AsyncQueryHandler handler, Integer taskId) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.ID + "=?", new String[]{String.valueOf(taskId)},
                TaskDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    /**
     * Get tasks for one Survey
     * @param handler
     * @param surveyId
     */
    public static void getTasksFromDBbySurveyId(AsyncQueryHandler handler, int surveyId) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.SURVEY_ID + "=?",
                new String[]{String.valueOf(surveyId)},
                TaskDbSchema.SORT_ORDER_DESC);
    }

    public static void getMyTasksFromDB(AsyncQueryHandler handler) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1",
                null, TaskDbSchema.SORT_ORDER_DESC);
    }

    public static void setHideTaskOnMapByID(AsyncQueryHandler handler, Integer taskId, Boolean isHide) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskDbSchema.Columns.IS_HIDE.getName(), isHide);

        handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE, null, TaskDbSchema.CONTENT_URI, contentValues,
                TaskDbSchema.Columns.ID + "=?", new String[]{String.valueOf(taskId)});
    }

    public static void setTask(AsyncQueryHandler handler, Task task) {
        handler.startUpdate(TaskDbSchema.Query.All.TOKEN_UPDATE, null, TaskDbSchema.CONTENT_URI, task.toContentValues(),
                TaskDbSchema.Columns.ID + "=?", new String[]{String.valueOf(task.getId())});
    }

    /**
     * Update task statusId
     *
     * @param taskId
     * @param statusId
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

        ArrayList<Task> tasks = convertCursorToTasksList(cursor);

        if (currentLocation != null) {
            for (Task task : tasks) {
                taskLocation.setLatitude(task.getLatitude());
                taskLocation.setLongitude(task.getLongitude());

                contentValues.put(TaskDbSchema.Columns.DISTANCE.getName(), currentLocation.distanceTo(taskLocation));

                String where = TaskDbSchema.Columns.ID + "=?";
                String[] whereArgs = new String[]{String.valueOf(task.getId())};

                resolver.update(TaskDbSchema.CONTENT_URI, contentValues, where, whereArgs);
            }
        }

        /*ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        for (Task task : tasks) {
            Location temp = new Location(LocationManager.NETWORK_PROVIDER);
            temp.setLatitude(task.getLatitude());
            temp.setLongitude(task.getLongitude());
            if (currentLocation != null) {
                task.setDistance(currentLocation.distanceTo(temp));
            }
            ops.add(ContentProviderOperation.newUpdate(TaskDbSchema.CONTENT_URI)
                    .withValues(task.toContentValues())
                    .withYieldAllowed(true)
                    .build());
        }
        try {
            resolver.applyBatch(AppContentProvider.CONTENT_AUTHORITY, ops);
        } catch (RemoteException e) {
            L.e(TAG, "RemoteException:" + e);
        } catch (OperationApplicationException e) {
            L.e(TAG, "OperationApplicationException:" + e);
        }
        L.i(TAG, "calculateTaskDistance: stop");*/
    }

    /**
     * Convert cursor to Task list
     *
     * @param cursor - all fields cursor
     * @return
     */
    public static ArrayList<Task> convertCursorToTasksList(Cursor cursor) {
        ArrayList<Task> result = new ArrayList<Task>();
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
     * @return
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
}
