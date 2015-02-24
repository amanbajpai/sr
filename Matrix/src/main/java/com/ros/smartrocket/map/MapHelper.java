package com.ros.smartrocket.map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.os.Build;
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
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.CustomInfoMapWindowAdapter;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.fragment.TransparentSupportBaiduMapFragment;
import com.ros.smartrocket.fragment.TransparentSupportMapFragment;
import com.ros.smartrocket.utils.FontUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.twotoasters.baiduclusterkraf.OnShowInfoWindowListener;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.OnInfoWindowClickDownstreamListener;
import com.twotoasters.clusterkraf.OnMarkerClickDownstreamListener;
import com.twotoasters.clusterkraf.Options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapHelper {
    private static final String TAG = MapHelper.class.getSimpleName();

    public static final float COORDINATE_OFFSET = 0.00004f;
    public static final float BAIDU_MAP_COORDINATE_OFFSET = 0.00008f;
    public static final int TRANSITION_DURATION = 500;
    //private String transitionInterpolator = LinearInterpolator.class.getCanonicalName();
    public static final int DIP_DISTANCE_TO_JOIN_CLUSTER = 50;
    public static final int ZOOM_TO_BOUNDS_ANIMATION_DURATION = 500;
    public static final int SHOW_INFO_WINDOW_ANIMATION_DURATION = 500;
    public static final double EXPAND_BOUNDS_FACTOR = 0.5d;

    private static final int CLUSTER_PAINT_ALPHA = 255;
    private static final int CLUSTER_SIZE_100 = 100;
    private static final int CLUSTER_SIZE_1000 = 1000;

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
            //baiduMap.setMaxAndMinZoomLevel(0, 19);
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

    public static com.twotoasters.baiduclusterkraf.Options getBaiduClusterkrafOptions(Activity activity, Keys.MapViewMode mode,
                                                                                      OnShowInfoWindowListener onShowInfoWindowListener,
                                                                                      com.twotoasters.baiduclusterkraf.OnMarkerClickDownstreamListener onMarkerClickListener) {
        com.twotoasters.baiduclusterkraf.Options options = new com.twotoasters.baiduclusterkraf.Options(activity);

        if (activity != null) {
            options.setTransitionDuration(MapHelper.TRANSITION_DURATION);
            options.setTransitionInterpolator(new LinearInterpolator());

            options.setPixelDistanceToJoinCluster(UIUtils.getPxFromDp(activity, 10));
            options.setZoomToBoundsAnimationDuration(MapHelper.ZOOM_TO_BOUNDS_ANIMATION_DURATION);
            options.setShowInfoWindowAnimationDuration(MapHelper.SHOW_INFO_WINDOW_ANIMATION_DURATION);
            options.setExpandBoundsFactor(MapHelper.EXPAND_BOUNDS_FACTOR);
            options.setSinglePointClickBehavior(com.twotoasters.baiduclusterkraf.Options.SinglePointClickBehavior.SHOW_INFO_WINDOW);
            options.setClusterClickBehavior(com.twotoasters.baiduclusterkraf.Options.ClusterClickBehavior.ZOOM_TO_BOUNDS);
            options.setClusterInfoWindowClickBehavior(com.twotoasters.baiduclusterkraf.Options.ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS);

            /*Live hack from library developers ^)*/
            options.setZoomToBoundsPadding(activity.getResources().getDrawable(R.drawable.ic_map_cluster_pin).getIntrinsicHeight());
            options.setMarkerOptionsChooser(new TaskBaiduOptionsChooser(activity));
            options.setOnMarkerClickDownstreamListener(onMarkerClickListener);

            //options.setOnInfoWindowClickListener(onInfoWindowClickListener);
            options.setOnShowInfoWindowListener(onShowInfoWindowListener);
            //options.setInfoWindowDownstreamAdapter(new CustomInfoMapWindowAdapter(activity, mode));
        }
        return options;
    }

    public static Options getGoogleClusterkrafOptions(Activity activity, Keys.MapViewMode mode,
                                                      OnInfoWindowClickDownstreamListener onInfoWindowClickListener,
                                                      OnMarkerClickDownstreamListener onMarkerClickListener) {
        Options options = new Options();

        if (activity != null) {
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
        }
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
                Task task = TasksBL.convertCursorToTaskOrNull(TasksBL.getTaskFromDBbyID(taskId));
                if (task != null) {
                    activity.startActivity(IntentUtils.getTaskDetailIntent(activity, taskId,
                            task.getStatusId(), TasksBL.isPreClaimTask(task)));
                }
        }
    }

    /**
     * Check coordinates of pins and change it if they are equals
     */
    public static ArrayList<InputPoint> getGoogleMapInputPointList(List<Task> list, Location location) {
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
                Double[] newTaskCoordinate = MapHelper.getEditedTaskCoordinate(list.size(),
                        latitude, longitude, MapHelper.COORDINATE_OFFSET, markerLocationMap);
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
    public static ArrayList<com.twotoasters.baiduclusterkraf.InputPoint> getBaiduMapInputPointList(List<Task> list, Location location) {
        ArrayList<com.twotoasters.baiduclusterkraf.InputPoint> inputPoints = new ArrayList<com.twotoasters.baiduclusterkraf.InputPoint>();
        Map<String, String> markerLocationMap = new HashMap<String, String>();

        for (int i = 0; i < list.size(); i++) {
            Task item = list.get(i);
            Double latitude = item.getLatitude();
            Double longitude = item.getLongitude();

            if (location != null && (latitude == null || longitude == null)) {
                latitude = location.getLatitude() + MapHelper.BAIDU_MAP_COORDINATE_OFFSET;
                longitude = location.getLongitude() + MapHelper.BAIDU_MAP_COORDINATE_OFFSET;
            }

            if (latitude != null && longitude != null) {
                Double[] newTaskCoordinate = MapHelper.getEditedTaskCoordinate(list.size(),
                        latitude, longitude, MapHelper.BAIDU_MAP_COORDINATE_OFFSET, markerLocationMap);
                if (newTaskCoordinate != null) {
                    item.setLatitude(newTaskCoordinate[0]);
                    item.setLongitude(newTaskCoordinate[1]);
                }

                inputPoints.add(new com.twotoasters.baiduclusterkraf.InputPoint(item.getBaiduLatLng(), item));
            }
        }

        Log.i(TAG, "[tasks.size=" + inputPoints.size() + "]");

        return inputPoints;
    }

    /**
     * Check coordinates of pins and change it if they are equals
     */
    public static Double[] getEditedTaskCoordinate(int itemSize, double latitude, double longitude, float coordinateOffset,
                                                   Map<String, String> markerLocationMap) {

        Double[] location = null;
        String locationToAdd = null;

        for (int i = 0; i <= itemSize; i++) {

            if (markerLocationMap.containsValue((latitude + i * coordinateOffset)
                    + "," + (longitude + i * coordinateOffset))) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                if (markerLocationMap.containsValue((latitude - i * coordinateOffset)
                        + "," + (longitude - i * coordinateOffset))) {
                    continue;

                } else {
                    location = new Double[2];
                    location[0] = latitude - (i * coordinateOffset);
                    location[1] = longitude - (i * coordinateOffset);
                    locationToAdd = (latitude - i * coordinateOffset) + "," + (longitude - i * coordinateOffset);
                    break;
                }

            } else {
                location = new Double[2];
                location[0] = latitude + (i * coordinateOffset);
                location[1] = longitude + (i * coordinateOffset);
                locationToAdd = (latitude + i * coordinateOffset) + "," + (longitude + i * coordinateOffset);
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

    public static Bitmap getClusterBitmap(Resources res, int resourceId, int clusterSize, Paint largePaint, Paint mediumPaint, Paint smallPaint) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
        if (!bitmap.isMutable()) {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

        Canvas canvas = new Canvas(bitmap);

        Paint paint;
        float originY;
        if (clusterSize < CLUSTER_SIZE_100) {
            paint = largePaint;
            originY = bitmap.getHeight() * 0.6f;
        } else if (clusterSize < CLUSTER_SIZE_1000) {
            paint = mediumPaint;
            originY = bitmap.getHeight() * 0.56f;
        } else {
            paint = smallPaint;
            originY = bitmap.getHeight() * 0.52f;
        }

        canvas.drawText(String.valueOf(clusterSize), bitmap.getWidth() * 0.5f, originY, paint);
        return bitmap;
    }

    public static Paint getMediumClasterPaint(Context context) {
        Paint clusterPaintMedium = new Paint();
        clusterPaintMedium.setColor(context.getResources().getColor(R.color.green));
        clusterPaintMedium.setAlpha(CLUSTER_PAINT_ALPHA);
        clusterPaintMedium.setTextAlign(Paint.Align.CENTER);
        clusterPaintMedium.setTypeface(FontUtils.loadFontFromAsset(context.getAssets(),
                FontUtils.getFontAssetPath(3)));
        clusterPaintMedium.setTextSize(context.getResources().getDimension(R.dimen.text_size_20sp));

        return clusterPaintMedium;
    }

    public static Paint getSmallClasterPaint(Context context) {
        Paint clusterPaintSmall = new Paint(getMediumClasterPaint(context));
        clusterPaintSmall.setTextSize(context.getResources().getDimension(R.dimen.text_size_16sp));
        return clusterPaintSmall;
    }

    public static Paint getLargeClasterPaint(Context context) {
        Paint clusterPaintLarge = new Paint(getMediumClasterPaint(context));
        clusterPaintLarge.setTextSize(context.getResources().getDimension(R.dimen.text_size_26sp));
        return clusterPaintLarge;
    }

    public interface SelectMapInterface {
        void useGoogleMap(GoogleMap googleMap);

        void useBaiduMap(BaiduMap baiduMap);
    }
}
