package com.matrix.bl;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.RemoteException;
import android.widget.ArrayAdapter;
import com.matrix.App;
import com.matrix.db.AppContentProvider;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Survey;
import com.matrix.db.entity.Task;
import com.matrix.location.MatrixLocationManager;
import com.matrix.utils.L;

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

    private Cursor getTasksFromDB() {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(TaskDbSchema.CONTENT_URI, TaskDbSchema.Query.All.PROJECTION,
                null, null, TaskDbSchema.SORT_ORDER_DESC);
        return cursor;
    }

    /**
     *
     * @param myLocation - user current location
     * @param cursor - Cursor with data set from DB
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


}
