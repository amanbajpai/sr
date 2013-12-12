package com.matrix.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import com.matrix.App;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Task;

import java.util.ArrayList;

/**
 * Created by bopr on 12/10/13.
 */
public class TasksBL {


    /**
     * 1. Get data from DB
     * 2. Update distance
     */
    public void recalculateTasksDistance(Location myLocation) {


    }

    private Cursor getTasksFromDB() {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(TaskDbSchema.CONTENT_URI, TaskDbSchema.Query.All.PROJECTION,
                null, null, TaskDbSchema.SORT_ORDER_DESC);
        return cursor;
    }

    public static void getTaskFromDB(AsyncQueryHandler handler, Integer taskId) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.ID + "=?", new String[]{String.valueOf(taskId)},
                TaskDbSchema.SORT_ORDER_DESC_LIMIT_1);
    }

    public static void getMyTasksFromDB(AsyncQueryHandler handler) {
        handler.startQuery(TaskDbSchema.Query.All.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.All.PROJECTION, TaskDbSchema.Columns.IS_MY + "=1",
                null, TaskDbSchema.SORT_ORDER_DESC);
    }

    /**
     * @param myLocation - user current location
     * @param cursor     - Cursor with data set from DB
     */
    private void calculateTaskDistance(Location myLocation, Cursor cursor) {
        ArrayList<Task> tasks = convertCursorToTasksList(cursor);
        App app = App.getInstance();
        ContentResolver resolver = app.getContentResolver();
        Location currentLocation = app.getLocationManager().getLocation();

        for (Task task : tasks) {
            Location temp = new Location(LocationManager.NETWORK_PROVIDER);
            temp.setLatitude(task.getLatitude());
            temp.setLongitude(task.getLongitude());
            if (currentLocation != null) {
                task.setDistance(currentLocation.distanceTo(temp));
            }

            //resolver.update(TaskDbSchema.CONTENT_URI, task.toContentValues());
        }
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
            cursor.moveToFirst();
            do {
                result.add(Task.fromCursor(cursor));
            } while (cursor.moveToNext());
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
