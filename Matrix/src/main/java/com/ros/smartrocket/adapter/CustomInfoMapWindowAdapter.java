package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.model.Marker;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.L;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.InfoWindowDownstreamAdapter;

public class CustomInfoMapWindowAdapter implements InfoWindowDownstreamAdapter {
    private static final String TAG = CustomInfoMapWindowAdapter.class.getSimpleName();
    private static final String MYLOC = "MyLoc";
    private final View mWindow;
    private final View mContents;
    private Keys.MapViewMode mode;

    public CustomInfoMapWindowAdapter(Activity activity, Keys.MapViewMode mode) {
        this.mode = mode;

        mWindow = activity.getLayoutInflater().inflate(R.layout.map_info_window, null);
        mContents = activity.getLayoutInflater().inflate(R.layout.map_info_contents, null);
    }

    private boolean render(Marker marker, View view, ClusterPoint clusterPoint) {
        boolean result = false;
        L.d(TAG, "render() [marker=" + marker + ", clusterPoint=" + clusterPoint + "]");
        L.d(TAG, "render() [title=" + marker.getTitle() + ", ID=" + marker.getId() + ", "
                + "snipped=" + marker.getSnippet() + ", offset=" + clusterPoint.getPointAtOffset(0) + "]");

        if (clusterPoint.getPointAtOffset(0) != null) {
            Task task = (Task) clusterPoint.getPointAtOffset(0).getTag();

            // Set Price prefix
            String title = task.getName();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            titleUi.setText(title);

            // Set Price prefix
            String prefix = "HK$";
            TextView prefixTitleUi = ((TextView) view.findViewById(R.id.price_label));
            prefixTitleUi.setText(prefix);

            // Set Price
            String price = "" + task.getPrice();
            TextView rateText = ((TextView) view.findViewById(R.id.price_value));
            rateText.setText(price);

            // Set Distance
            String distance = "" + task.getDistance();
            TextView distanceText = ((TextView) view.findViewById(R.id.distance_value));
            distanceText.setText(distance);

            result = true;
        }

        return result;
    }

    @Override
    public View getInfoContents(Marker marker, ClusterPoint clusterPoint) {
        View view = null;
        // Don't show popup window in such cases
        if (marker != null && !MYLOC.equals(marker.getSnippet())
                && mode != Keys.MapViewMode.SINGLETASK) {
            if (render(marker, mContents, clusterPoint)) {
                view = mContents;
            }
        }
        return view;
    }

    @Override
    public View getInfoWindow(Marker marker, ClusterPoint clusterPoint) {
        View view = null;
        // Don't show popup window in such cases
        if (marker != null && !MYLOC.equals(marker.getSnippet())
                && mode != Keys.MapViewMode.SINGLETASK) {
            if (render(marker, mWindow, clusterPoint)) {
                view = mWindow;
            }
        }
        return view;
    }
}