package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.View;
import com.google.android.gms.maps.model.Marker;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.map.MapHelper;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.InfoWindowDownstreamAdapter;

public class CustomInfoMapWindowAdapter implements InfoWindowDownstreamAdapter {
    private static final String MY_LOCATION = "MyLoc";
    private final View mWindow;
    //private final View mContents;
    private Keys.MapViewMode mode;
    private Activity activity;
    private AsyncQueryHandler handler;

    public CustomInfoMapWindowAdapter(Activity activity, Keys.MapViewMode mode) {
        this.mode = mode;
        this.activity = activity;


        handler = new DbHandler(activity.getContentResolver());
        mWindow = activity.getLayoutInflater().inflate(R.layout.map_info_window, null);
        //mContents = activity.getLayoutInflater().inflate(R.layout.map_info_contents, null);
    }

    private boolean render(Marker marker, View view, ClusterPoint clusterPoint) {
        if (clusterPoint != null && clusterPoint.getPointAtOffset(0) != null) {
            Task task = (Task) clusterPoint.getPointAtOffset(0).getTag();

            final Task updatedTask = TasksBL.convertCursorToTaskOrNull(TasksBL.getTaskFromDBbyID(task.getId()));
            //TODO Get data from local DB
            //TasksBL.getTaskFromDBbyID(handler, task.getId(), view);
            if (updatedTask != null) {
                MapHelper.setMapOverlayView(activity, view, updatedTask);
                return true;
            }
        }

        return false;
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    View windowView = (View) cookie;
                    Task task = TasksBL.convertCursorToTaskOrNull(cursor);

                    if (task != null) {
                        MapHelper.setMapOverlayView(activity, windowView, task);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View getInfoContents(Marker marker, ClusterPoint clusterPoint) {
        /*View view = null;

        if (marker != null && !MY_LOCATION.equals(marker.getSnippet())
                && mode != Keys.MapViewMode.SINGLE_TASK && render(marker, mContents, clusterPoint)) {
            view = mContents;

        }*/
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker, ClusterPoint clusterPoint) {
        View view = null;

        if (marker != null && !MY_LOCATION.equals(marker.getSnippet())
                && mode != Keys.MapViewMode.SINGLE_TASK && render(marker, mWindow, clusterPoint)) {
            view = mWindow;
        }
        return view;
    }
}