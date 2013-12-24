package com.ros.smartrocket.bl;

import android.content.*;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.RemoteException;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.AppContentProvider;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.L;

import java.util.ArrayList;

/**
 * Created by bopr on 12/10/13.
 */
public class TasksBL {


    private static final String TAG = TasksBL.class.getSimpleName();

    /**
     * 1. Get data from DB
     * 2. Update distance
     */
    public void recalculateTasksDistance(Location myLocation) {
        Cursor cursor = getTasksFromDB();
        calculateTaskDistance(myLocation, cursor);
    }

    private static Cursor getTasksFromDB() {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(TaskDbSchema.CONTENT_URI, TaskDbSchema.Query.All.PROJECTION,
                null, null, TaskDbSchema.SORT_ORDER_DESC);
        return cursor;
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
     * @param myLocation - user current location
     * @param cursor     - Cursor with data set from DB
     */

    private void calculateTaskDistance(Location myLocation, Cursor cursor) {
        L.i(TAG, "calculateTaskDistance: start");
        ArrayList<Task> tasks = convertCursorToTasksList(cursor);
        L.i(TAG, "calculateTaskDistance: [tasks.size=" + tasks.size() + "]");
        App app = App.getInstance();
        ContentResolver resolver = app.getContentResolver();
        Location currentLocation = app.getLocationManager().getLocation();

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
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
        L.i(TAG, "calculateTaskDistance: stop");
    }

    /**
     * Conveert cursor to Task list
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
