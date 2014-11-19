package com.ros.smartrocket.map;

import android.app.Activity;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.CustomInfoMapWindowAdapter;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.fragment.TransparentSupportBaiduMapFragment;
import com.ros.smartrocket.fragment.TransparentSupportMapFragment;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.OnInfoWindowClickDownstreamListener;
import com.twotoasters.clusterkraf.OnMarkerClickDownstreamListener;
import com.twotoasters.clusterkraf.Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapHelper {
    private static final String TAG = MapHelper.class.getSimpleName();

    public static final float COORDINATE_OFFSET = 0.00004f;
    public static final int TRANSITION_DURATION = 500;
    //private String transitionInterpolator = LinearInterpolator.class.getCanonicalName();
    public static final int DIP_DISTANCE_TO_JOIN_CLUSTER = 50;
    public static final int ZOOM_TO_BOUNDS_ANIMATION_DURATION = 500;
    public static final int SHOW_INFO_WINDOW_ANIMATION_DURATION = 500;
    public static final double EXPAND_BOUNDS_FACTOR = 0.5d;

    public static BaiduMap getBaiduMap(FragmentActivity activity, float zoomLevel) {
        BaiduMap baiduMap = null;
        MapStatus ms = new MapStatus.Builder().build();

        BaiduMapOptions uiSettings = new BaiduMapOptions();
        uiSettings.mapStatus(ms);
        uiSettings.compassEnabled(false);
        uiSettings.zoomControlsEnabled(false);
        uiSettings.zoomGesturesEnabled(true);
        uiSettings.scaleControlEnabled(true);

        TransparentSupportBaiduMapFragment mapFragment = (TransparentSupportBaiduMapFragment) activity
                .getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            baiduMap = mapFragment.getBaiduMap();
            baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            baiduMap.setMaxAndMinZoomLevel(1, 19);
                    /*baiduMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition pos) {
                            zoomLevel = pos.zoom;
                        }
                    });*/

            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_icon_small);
            baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, mCurrentMarker));
            baiduMap.setMyLocationEnabled(true);
        }

        return baiduMap;
    }

    public static GoogleMap getGoogleMap(FragmentActivity activity, GoogleMap.OnCameraChangeListener cameraChangeListener) {
        GoogleMap googleMap = null;
        TransparentSupportMapFragment mapFragment = (TransparentSupportMapFragment) activity
                .getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            googleMap = mapFragment.getMap();
            if (googleMap != null) {
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setAllGesturesEnabled(false);
                uiSettings.setScrollGesturesEnabled(true);
                uiSettings.setZoomGesturesEnabled(true);
                uiSettings.setIndoorLevelPickerEnabled(false);
                googleMap.setIndoorEnabled(false);
                googleMap.setOnCameraChangeListener(cameraChangeListener);
            }
        }

        return googleMap;
    }

    public static boolean isMapNotNull(GoogleMap googleMap, BaiduMap baiduMap) {
        boolean result;
        if (Config.USE_BAIDU) {
            result = baiduMap != null;
        } else {
            result = googleMap != null;
        }
        return result;
    }

    public static void mapChooser(GoogleMap googleMap, BaiduMap baiduMap, SelectMapInterface selectMapInterface) {
        if (isMapNotNull(googleMap, baiduMap)) {
            if (Config.USE_BAIDU) {
                selectMapInterface.useBaiduMap(baiduMap);
            } else {
                selectMapInterface.useGoogleMap(googleMap);
            }
        }
    }

    public static Options getGoogleClusterkrafOptions(Activity activity, Keys.MapViewMode mode,
                                                      OnInfoWindowClickDownstreamListener onInfoWindowClickListener,
                                                      OnMarkerClickDownstreamListener onMarkerClickListener) {
        Options options = new Options();
        options.setTransitionDuration(MapHelper.TRANSITION_DURATION);
        options.setTransitionInterpolator(new LinearInterpolator());

        options.setPixelDistanceToJoinCluster(UIUtils.getPxFromDp(activity, MapHelper.DIP_DISTANCE_TO_JOIN_CLUSTER));
        options.setZoomToBoundsAnimationDuration(MapHelper.ZOOM_TO_BOUNDS_ANIMATION_DURATION);
        options.setShowInfoWindowAnimationDuration(MapHelper.SHOW_INFO_WINDOW_ANIMATION_DURATION);
        options.setExpandBoundsFactor(MapHelper.EXPAND_BOUNDS_FACTOR);
        options.setSinglePointClickBehavior(Options.SinglePointClickBehavior.SHOW_INFO_WINDOW);
        options.setClusterClickBehavior(Options.ClusterClickBehavior.ZOOM_TO_BOUNDS);
        options.setClusterInfoWindowClickBehavior(Options.ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS);

        /*Live hack from library developers ^)*/
        options.setZoomToBoundsPadding(activity.getResources().getDrawable(R.drawable.ic_map_cluster_pin).getIntrinsicHeight());
        options.setMarkerOptionsChooser(new TaskOptionsChooser(activity));
        options.setOnMarkerClickDownstreamListener(onMarkerClickListener);

        options.setOnInfoWindowClickDownstreamListener(onInfoWindowClickListener);
        options.setInfoWindowDownstreamAdapter(new CustomInfoMapWindowAdapter(activity, mode));

        return options;
    }

    public static void setMapOverlayView(Activity activity, View view, Task task) {
        LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);

        ImageView typeIcon = (ImageView) view.findViewById(R.id.typeIcon);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView priceText = (TextView) view.findViewById(R.id.price_value);
        TextView pointText = (TextView) view.findViewById(R.id.point_value);
        TextView distanceText = (TextView) view.findViewById(R.id.distance_value);

        UIUtils.showWaveTypeIcon(activity, typeIcon, task.getIcon());

        title.setText(task.getName());
        priceText.setText(UIUtils.getBalanceOrPrice(activity, task.getPrice(), task.getCurrencySign(), null, null));
        pointText.setText(String.format(Locale.US, "%.0f", task.getExperienceOffer()));
        distanceText.setText(UIUtils.convertMToKm(activity, task.getDistance(),
                R.string.map_popup_distance, false));

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
            case CLAIMED:
            case STARTED:
                if (TasksBL.isPreClaimTask(task)) {
                    mainLayout.setBackgroundResource(R.drawable.popup_violet);
                    priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_violet, 0, 0, 0);
                    pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_violet, 0, 0, 0);
                    distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_violet, 0, 0, 0);
                } else {
                    mainLayout.setBackgroundResource(R.drawable.popup_green);
                    priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_green, 0, 0, 0);
                    pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_green, 0, 0, 0);
                    distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_green, 0, 0, 0);
                }
                break;
            case SCHEDULED:
            case PENDING:
                mainLayout.setBackgroundResource(R.drawable.popup_blue);
                priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_blue, 0, 0, 0);
                pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_blue, 0, 0, 0);
                distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_blue, 0, 0, 0);
                break;
            case COMPLETED:
            case VALIDATION:
                mainLayout.setBackgroundResource(R.drawable.popup_grey);
                priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_lightgrey, 0, 0, 0);
                pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_lightgrey, 0, 0, 0);
                distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_lightgrey, 0, 0, 0);
                break;
            case RE_DO_TASK:
                mainLayout.setBackgroundResource(R.drawable.popup_red);
                priceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_red, 0, 0, 0);
                pointText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_red, 0, 0, 0);
                distanceText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.human_red, 0, 0, 0);
                break;
            default:
                break;
        }
    }

    public static void mapOverlayClickResult(Activity activity, int taskId, int taskStatusId) {
        switch (TasksBL.getTaskStatusType(taskStatusId)) {
            case SCHEDULED:
                activity.startActivity(IntentUtils.getTaskValidationIntent(activity, taskId, false, false));
                break;
                /*case RE_DO_TASK:
                    startActivity(IntentUtils.getQuestionsIntent(getActivity(), taskId));
                    break;*/
            default:
                activity.startActivity(IntentUtils.getTaskDetailIntent(activity, taskId));
        }
    }

    /**
     * Check coordinates of pins and change it if they are equals
     */
    public static ArrayList<InputPoint> getInputPointList(List<Task> list, Location location) {
        ArrayList<InputPoint> inputPoints = new ArrayList<InputPoint>();
        Map<String, String> markerLocationMap = new HashMap<String, String>();

        for (int i = 0; i < list.size(); i++) {
            Task item = list.get(i);
            Double latitude = item.getLatitude();
            Double longitude = item.getLongitude();

            if (location != null && (latitude == null || longitude == null)) {
                latitude = location.getLatitude() + MapHelper.COORDINATE_OFFSET;
                longitude = location.getLongitude() + MapHelper.COORDINATE_OFFSET;
            }

            if (latitude != null && longitude != null) {
                Double[] newTaskCoordinate = MapHelper.getEditedTaskCoordinate(list.size(), latitude, longitude, markerLocationMap);
                if (newTaskCoordinate != null) {
                    item.setLatitude(newTaskCoordinate[0]);
                    item.setLongitude(newTaskCoordinate[1]);
                }
                inputPoints.add(new InputPoint(item.getLatLng(), item));
            }
        }

        Log.i(TAG, "[tasks.size=" + inputPoints.size() + "]");

        return inputPoints;
    }

    /**
     * Check coordinates of pins and change it if they are equals
     */
    public static Double[] getEditedTaskCoordinate(int itemSize, double latitude, double longitude,
                                                   Map<String, String> markerLocationMap) {

        Double[] location = null;
        String locationToAdd = null;

        for (int i = 0; i <= itemSize; i++) {

            if (markerLocationMap.containsValue((latitude + i * COORDINATE_OFFSET)
                    + "," + (longitude + i * COORDINATE_OFFSET))) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                if (markerLocationMap.containsValue((latitude - i * COORDINATE_OFFSET)
                        + "," + (longitude - i * COORDINATE_OFFSET))) {
                    continue;

                } else {
                    location = new Double[2];
                    location[0] = latitude - (i * COORDINATE_OFFSET);
                    location[1] = longitude - (i * COORDINATE_OFFSET);
                    locationToAdd = (latitude - i * COORDINATE_OFFSET) + "," + (longitude - i * COORDINATE_OFFSET);
                    break;
                }

            } else {
                location = new Double[2];
                location[0] = latitude + (i * COORDINATE_OFFSET);
                location[1] = longitude + (i * COORDINATE_OFFSET);
                locationToAdd = (latitude + i * COORDINATE_OFFSET) + "," + (longitude + i * COORDINATE_OFFSET);
                break;
            }
        }

        if (location != null) {
            markerLocationMap.put(locationToAdd, locationToAdd);
        }

        return location;
    }

    public static float getZoomForMetersWide(float mapWidth, final double desiredMeters, final double latitude) {
        final double latitudinalAdjustment = Math.cos(Math.PI * latitude / 180.0);

        final double arg = (40075004 * mapWidth * latitudinalAdjustment / (desiredMeters * 256.0));

        return (float) (Math.log(arg) / Math.log(2.0));
    }

    public static TileProvider getTileProvider() {
        return new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String s = String.format("http://ditu.google.cn/images/%d/%d/%d.png", zoom, x, y);

                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }

                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }

            private boolean checkTileExists(int x, int y, int zoom) {
                int minZoom = 12;
                int maxZoom = 16;

                if ((zoom < minZoom || zoom > maxZoom)) {
                    return false;
                }

                return true;
            }
        };

    }

    public interface SelectMapInterface {
        void useGoogleMap(GoogleMap googleMap);

        void useBaiduMap(BaiduMap baiduMap);
    }
}