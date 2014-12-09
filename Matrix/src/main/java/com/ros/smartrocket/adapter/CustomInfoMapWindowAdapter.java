package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.map.MapHelper;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.InfoWindowDownstreamAdapter;

public class CustomInfoMapWindowAdapter implements InfoWindowDownstreamAdapter {
    private static final String MY_LOCATION = "MyLoc";
    private final View mWindow;
    private final View mContents;
    private Keys.MapViewMode mode;
    private Activity activity;

    public CustomInfoMapWindowAdapter(Activity activity, Keys.MapViewMode mode) {
        this.mode = mode;
        this.activity = activity;

        mWindow = activity.getLayoutInflater().inflate(R.layout.map_info_window, null);
        mContents = activity.getLayoutInflater().inflate(R.layout.map_info_contents, null);
    }

    private boolean render(Marker marker, View view, ClusterPoint clusterPoint) {
        boolean result = false;

        if (clusterPoint != null && clusterPoint.getPointAtOffset(0) != null) {
            Task task = (Task) clusterPoint.getPointAtOffset(0).getTag();

            MapHelper.setMapOverlayView(activity, view, task);

            result = true;
        }

        return result;
    }

    @Override
    public View getInfoContents(Marker marker, ClusterPoint clusterPoint) {
        View view = null;

        if (marker != null && !MY_LOCATION.equals(marker.getSnippet())
                && mode != Keys.MapViewMode.SINGLE_TASK && render(marker, mContents, clusterPoint)) {
            view = mContents;

        }
        return view;
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