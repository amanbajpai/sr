package com.ros.smartrocket.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.Marker;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.InfoWindowDownstreamAdapter;

import java.util.Locale;

public class CustomInfoMapWindowAdapter implements InfoWindowDownstreamAdapter {
    private static final String TAG = CustomInfoMapWindowAdapter.class.getSimpleName();
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
        L.d(TAG, "render() [marker=" + marker + ", clusterPoint=" + clusterPoint + "]");
        L.d(TAG, "render() [title=" + marker.getTitle() + ", ID=" + marker.getId() + ", "
                + "snipped=" + marker.getSnippet() + ", offset=" + clusterPoint.getPointAtOffset(0) + "]");

        if (clusterPoint.getPointAtOffset(0) != null) {
            Task task = (Task) clusterPoint.getPointAtOffset(0).getTag();

            LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);

            ImageView typeIcon = (ImageView) view.findViewById(R.id.typeIcon);

            TextView title = (TextView) view.findViewById(R.id.title);
            TextView priceText = (TextView) view.findViewById(R.id.price_value);
            TextView pointText = (TextView) view.findViewById(R.id.point_value);
            TextView distanceText = (TextView) view.findViewById(R.id.distance_value);

            UIUtils.showWaveTypeIcon(activity, typeIcon, task.getIcon());

            title.setText(task.getName());
            priceText.setText(UIUtils.getBalanceOrPrice(activity, task.getPrice(), task.getCurrencySign()));
            pointText.setText(String.format(Locale.US, "%.0f", task.getExperienceOffer()));
            distanceText.setText(UIUtils.convertMToKm(activity, task.getDistance(),
                    R.string.map_popup_distance, false));

            switch (TasksBL.getTaskStatusType(task.getStatusId())) {
                case none:
                case claimed:
                case started:
                    mainLayout.setBackgroundResource(R.drawable.popup_green);
                    priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_green, 0, 0, 0);
                    pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_green, 0, 0, 0);
                    distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_green, 0, 0, 0);
                    break;
                case scheduled:
                case pending:
                    mainLayout.setBackgroundResource(R.drawable.popup_blue);
                    priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_blue, 0, 0, 0);
                    pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_blue, 0, 0, 0);
                    distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_blue, 0, 0, 0);
                    break;
                case completed:
                case validation:
                    mainLayout.setBackgroundResource(R.drawable.popup_grey);
                    priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_lightgrey, 0, 0, 0);
                    pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_lightgrey, 0, 0, 0);
                    distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_lightgrey, 0, 0, 0);
                    break;
                case reDoTask:
                    mainLayout.setBackgroundResource(R.drawable.popup_red);
                    priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_red, 0, 0, 0);
                    pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_red, 0, 0, 0);
                    distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_red, 0, 0, 0);
                    break;

                default:
                    mainLayout.setBackgroundResource(R.drawable.popup_green);
                    priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_green, 0, 0, 0);
                    pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_green, 0, 0, 0);
                    distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_green, 0, 0, 0);
                    break;
            }

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