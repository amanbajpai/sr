package com.matrix.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.*;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.*;
import com.matrix.App;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.activity.TaskDetailsActivity;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.Task;
import com.matrix.location.MatrixLocationManager;
import com.matrix.utils.L;
import com.matrix.utils.UIUtils;
import com.twotoasters.clusterkraf.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TasksMapFragment extends Fragment {

    private static final String TAG = TasksMapFragment.class.getSimpleName();
    private static final String MYLOC = "MyLoc";
    private MatrixLocationManager lm;
    private View fragmentView;
    private ImageButton btnFilter;
    private ImageButton btnMyLocation;
    private RelativeLayout rlFilterPanel;
    private boolean isFilterShow = false;
    private GoogleMap map;
    private CameraPosition restoreCameraPosition;
    public static int taskRadius = 5000;
    private int sbRadiusProgress = 10;
    private int sbRadiusDelta = 500; // 1% = 500m => Max = 50km
    private TextView txtRadius;
    private float defaultZoomLevel = 11f;
    private float zoomLevel = defaultZoomLevel;
    private SeekBar sbRadius;


    private boolean mLoading = false;

    private ArrayList<InputPoint> inputPoints;
    private Clusterkraf clusterkraf;
    private ClusterOptions options;

    private AsyncQueryHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputPoints = new ArrayList<InputPoint>();
        if (this.options == null) {
            this.options = new ClusterOptions();
        }
        handler = new DbHandler(getActivity().getContentResolver());

        lm = App.getInstance().getLocationManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() [fragmentView  =  " + fragmentView + ", savedInstanceState=" + savedInstanceState + "]");

        fragmentView = inflater.inflate(R.layout.fragment_map, null);

        btnFilter = (ImageButton) fragmentView.findViewById(R.id.btnFilter);
        btnMyLocation = (ImageButton) fragmentView.findViewById(R.id.btnMyLocation);
        rlFilterPanel = (RelativeLayout) fragmentView.findViewById(R.id.hidden_panel);
        sbRadius = (SeekBar) rlFilterPanel.findViewById(R.id.seekBarRadius);
        txtRadius = (TextView) rlFilterPanel.findViewById(R.id.txtRadius);
        this.setRaiusText();
        sbRadius.setProgress(10);
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sbRadiusProgress = progress;
                taskRadius = sbRadiusDelta * sbRadiusProgress;
                updateMapPins();
                setRaiusText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setHasOptionsMenu(true);

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCameraToMyLocation();
            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togleFilterPannel();
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();

        Location location = lm.getLocation();
        if (location != null) {
            loadTasks(location);
            addMyLocationAndRadius(location, taskRadius);
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.looking_for_location);
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    L.i(TAG, "Location Updated!");
                    loadTasks(location);
                    addMyLocationAndRadius(location, taskRadius);
                }
            });
        }
        showFilterPannel(false);
    }


    private void showTaskDetails(Long taskId) {
        Intent intent = new Intent(getActivity(), TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        startActivity(intent);
    }

    public void onLoadingComplete(ArrayList<Task> list) {
        inputPoints.clear();
        for (Task item : list) {
            if (item.getLatitude() != null && item.getLongitude() != null) {
                inputPoints.add(new InputPoint(item.getLatLng(), item));
            }
        }
        Log.i(TAG, "[tasks.size=" + inputPoints.size() + "]");
        if (inputPoints.size() > 0) {
            initClusterkraf();
        } else if (getActivity() != null) {
            UIUtils.showSimpleToast(getActivity(), R.string.no_tasks_found, Toast.LENGTH_LONG);
        }
    }

    public void loadTasks(Location location) {
        if (location != null) {
            loadTasks(location.getLatitude(), location.getLongitude(), taskRadius, 100);
        }
    }

    public void loadTasks(double latitude, double longitude, int radius, int limit) {
        if (mLoading) {
            return;
        }

        L.i(TAG, "Get Tasks() ...");
        getTasks();
    }

    private void getTasks() {
        handler.startQuery(TaskDbSchema.Query.TOKEN_QUERY, null, TaskDbSchema.CONTENT_URI,
                TaskDbSchema.Query.PROJECTION, null, null, TaskDbSchema.SORT_ORDER_DESC);
    }

    /* ==============================================
    * Methods for Clusters pins display on the map
    * ============================================== */

    /**
     * Initialize Google Map
     */
    private void initMap() {
        if (map == null) {
            TransparentSupportMapFragment mapFragment = (TransparentSupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                map = mapFragment.getMap();
                if (map != null) {
                    UiSettings uiSettings = map.getUiSettings();
                    uiSettings.setAllGesturesEnabled(false);
                    uiSettings.setScrollGesturesEnabled(true);
                    uiSettings.setZoomGesturesEnabled(true);
                    map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition pos) {
                            zoomLevel = pos.zoom;
                            L.d(TAG, "ZoomLevel = " + zoomLevel);
                        }
                    });
                }
            }
        } else {
            moveMapCameraToBoundsAndInitClusterkraf();
        }
    }

    /**
     * Move Camera to position
     */
    private void moveMapCameraToBoundsAndInitClusterkraf() {
        if (map != null && options != null && inputPoints != null) {
            L.d(TAG, "moveMapCameraToBoundsAndInitClusterkraf()");
            try {
                moveCameraToMyLocation();
                initClusterkraf();
            } catch (IllegalStateException ise) {
                L.e(TAG, "moveMapCameraToBoundsAndInitClusterkraf()" + ise);
            }
        }
    }

    /**
     * Inirialize Cluster library and add pins
     */
    private void initClusterkraf() {
        if (this.map != null && this.inputPoints != null && this.inputPoints.size() > 0) {
            com.twotoasters.clusterkraf.Options options = new com.twotoasters.clusterkraf.Options();
            applyemoApplicationOptionsToClusterkrafOptions(options);
            // customize the options before you construct a Clusterkraf instance
            this.clusterkraf = new Clusterkraf(this.map, options, inputPoints);
        }
    }

    /**
     * Applies the sample.SampleActivity.Options chosen in Normal or Advanced
     * mode menus to the clusterkraf.Options which will be used to construct our
     * Clusterkraf instance
     *
     * @param options
     */
    private void applyemoApplicationOptionsToClusterkrafOptions(com.twotoasters.clusterkraf.Options options) {
        options.setTransitionDuration(this.options.transitionDuration);

        /* Hardcoded Transaction type to avoid */
        options.setTransitionInterpolator(new LinearInterpolator());

        options.setPixelDistanceToJoinCluster(getPixelDistanceToJoinCluster());
        options.setZoomToBoundsAnimationDuration(this.options.zoomToBoundsAnimationDuration);
        options.setShowInfoWindowAnimationDuration(this.options.showInfoWindowAnimationDuration);
        options.setExpandBoundsFactor(this.options.expandBoundsFactor);
        options.setSinglePointClickBehavior(this.options.singlePointClickBehavior);
        options.setClusterClickBehavior(this.options.clusterClickBehavior);
        options.setClusterInfoWindowClickBehavior(this.options.clusterInfoWindowClickBehavior);

        /*Live hack from library developers ^)*/
        options.setZoomToBoundsPadding(getResources().getDrawable(R.drawable.ic_map_cluster_pin).getIntrinsicHeight());

        options.setMarkerOptionsChooser(new TaskOptionsChooser(getActivity()));
        options.setOnMarkerClickDownstreamListener(onMarkerClickListener);
        options.setOnInfoWindowClickDownstreamListener(onInfoWindowClickListener);
        options.setInfoWindowDownstreamAdapter(new CustomInfoWindowAdapter());
        //options.setProcessingListener(this);
    }

    /**
     * Help util method to get px for Cluster icon
     *
     * @return
     */
    private int getPixelDistanceToJoinCluster() {
        return convertDeviceIndependentPixelsToPixels(this.options.dipDistanceToJoinCluster);
    }

    /**
     * @param dip
     * @return
     */
    private int convertDeviceIndependentPixelsToPixels(int dip) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return Math.round(displayMetrics.density * dip);
    }

    /**
     *
     */
    private OnMarkerClickDownstreamListener onMarkerClickListener = new OnMarkerClickDownstreamListener() {
        @Override
        public boolean onMarkerClick(Marker marker, ClusterPoint clusterPoint) {
            L.d(TAG, "onMarkerClick() " + marker.getTitle() + ", ID=" + marker.getId() + ", " +
                    "snipped=" + marker.getSnippet() + "]");
            boolean result = false;
            return result;
        }
    };

    /**
     *
     */
    private OnInfoWindowClickDownstreamListener onInfoWindowClickListener = new OnInfoWindowClickDownstreamListener() {
        @Override
        public boolean onInfoWindowClick(Marker marker, ClusterPoint clusterPoint) {
            L.d(TAG, "onInfoWindowClick() " + marker.getTitle() + ", ID=" + marker.getId() + "]");
            Long taskId = Long.valueOf(marker.getSnippet());
            showTaskDetails(taskId);
            return false;
        }
    };

    /**
     * Chooser image for cluster and pin
     */
    private class TaskOptionsChooser extends MarkerOptionsChooser {
        private final WeakReference<Context> contextRef;
        private final Paint clusterPaintLarge;
        private final Paint clusterPaintMedium;
        private final Paint clusterPaintSmall;

        public TaskOptionsChooser(Context context) {
            this.contextRef = new WeakReference<Context>(context);

            Resources res = context.getResources();

            clusterPaintMedium = new Paint();
            clusterPaintMedium.setColor(Color.WHITE);
            clusterPaintMedium.setAlpha(255);
            clusterPaintMedium.setTextAlign(Paint.Align.CENTER);
            clusterPaintMedium.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
            clusterPaintMedium.setTextSize(res.getDimension(R.dimen.text_size_medium));

            clusterPaintSmall = new Paint(clusterPaintMedium);
            clusterPaintSmall.setTextSize(res.getDimension(R.dimen.text_size_small));

            clusterPaintLarge = new Paint(clusterPaintMedium);
            clusterPaintLarge.setTextSize(res.getDimension(R.dimen.text_size_large));
        }

        @Override
        public void choose(MarkerOptions markerOptions, ClusterPoint clusterPoint) {
            L.d(TAG, "choose() [size=" + clusterPoint.size() + "]");
            Context context = contextRef.get();
            if (context != null) {
                Resources res = context.getResources();
                boolean isCluster = clusterPoint.size() > 1;
                BitmapDescriptor icon;
                String title;
                if (isCluster) {
                    int clusterSize = clusterPoint.size();
                    icon = BitmapDescriptorFactory.fromBitmap(getClusterBitmap(res, R.drawable.ic_map_cluster_pin, clusterSize));
                    title = "" + clusterSize;
                } else {
                    Task data = (Task) clusterPoint.getPointAtOffset(0).getTag();
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin);
                    title = data.getName();
                    markerOptions.snippet("" + data.getId());
                }
                markerOptions.icon(icon);
                markerOptions.title(title);
                markerOptions.anchor(0.5f, 1.0f);
            }
        }

        private Bitmap getClusterBitmap(Resources res, int resourceId, int clusterSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                options.inMutable = true;
            }
            Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
            if (bitmap.isMutable() == false) {
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            }

            Canvas canvas = new Canvas(bitmap);

            Paint paint = null;
            float originY;
            if (clusterSize < 100) {
                paint = clusterPaintLarge;
                originY = bitmap.getHeight() * 0.64f;
            } else if (clusterSize < 1000) {
                paint = clusterPaintMedium;
                originY = bitmap.getHeight() * 0.6f;
            } else {
                paint = clusterPaintSmall;
                originY = bitmap.getHeight() * 0.56f;
            }

            canvas.drawText(String.valueOf(clusterSize), bitmap.getWidth() * 0.5f, originY, paint);

            return bitmap;
        }
    }

    /**
     * Settings for Cluster library
     */
    private static class ClusterOptions {
        // clusterkraf library options
        private int transitionDuration = 500;
        private String transitionInterpolator = LinearInterpolator.class.getCanonicalName();
        private int dipDistanceToJoinCluster = 100;
        private int zoomToBoundsAnimationDuration = 500;
        private int showInfoWindowAnimationDuration = 500;
        private double expandBoundsFactor = 0.5d;
        private com.twotoasters.clusterkraf.Options.SinglePointClickBehavior singlePointClickBehavior = com
                .twotoasters.clusterkraf.Options.SinglePointClickBehavior.SHOW_INFO_WINDOW;
        private Options.ClusterClickBehavior clusterClickBehavior = Options.ClusterClickBehavior.ZOOM_TO_BOUNDS;
        private Options.ClusterInfoWindowClickBehavior clusterInfoWindowClickBehavior = Options.ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS;
    }

    /**
     * Demonstrates customizing the info window and/or its contents.
     */
    private class CustomInfoWindowAdapter implements InfoWindowDownstreamAdapter {
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getActivity().getLayoutInflater().inflate(R.layout.map_info_window, null);
            mContents = getActivity().getLayoutInflater().inflate(R.layout.map_info_contents, null);
        }

        private void render(Marker marker, View view, ClusterPoint clusterPoint) {
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

        }

        @Override
        public View getInfoContents(Marker marker, ClusterPoint clusterPoint) {
            if (!marker.getSnippet().equals(MYLOC)) {
                render(marker, mContents, clusterPoint);
                return mContents;
            } else {
                return null;
            }
        }

        @Override
        public View getInfoWindow(Marker marker, ClusterPoint clusterPoint) {
            if (!marker.getSnippet().equals(MYLOC)) {
                render(marker, mWindow, clusterPoint);
                return mWindow;
            } else {
                return null;
            }
        }
    }

    /**
     * Database Helper for Data fetching
     */
    private class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.TOKEN_QUERY:
                    ArrayList<Task> tasks = new ArrayList<Task>();

                    if (cursor != null) {
                        cursor.moveToFirst();
                        do {
                            tasks.add(Task.fromCursor(cursor));
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                    onLoadingComplete(tasks);
                    break;
            }
        }
    }

    private void togleFilterPannel() {
        showFilterPannel(!isFilterShow);
    }

    private void showFilterPannel(boolean show) {
        this.isFilterShow = show;

        if (isFilterShow) {
            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.map_filter_up);
            rlFilterPanel.startAnimation(bottomUp);
            rlFilterPanel.setVisibility(View.VISIBLE);
        } else {
            Animation bottomDown = AnimationUtils.loadAnimation(getActivity(), R.anim.map_filter_down);
            rlFilterPanel.startAnimation(bottomDown);
            rlFilterPanel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Draw circle on the map
     *
     * @param coordinates
     * @param radius
     */
    private void addCircle(LatLng coordinates, int radius, int strokeColor, int fillColor) {
        // Add a circle in Sydney
        Circle circle = map.addCircle(new CircleOptions()
                .center(coordinates)
                .radius(radius)
                .strokeColor(strokeColor)
                .strokeWidth(5f)
                .fillColor(fillColor));
    }

    /**
     * Add marker with myu location on the map
     *
     * @param coordinates
     */
    private void addMyLocation(LatLng coordinates) {
        L.d(TAG, "addMyLocation");
        map.addMarker(new MarkerOptions()
                .snippet(MYLOC)
                .position(coordinates)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
        );
        restoreCameraPosition = new CameraPosition(coordinates, zoomLevel, 0, 0);
        moveCameraToMyLocation();
    }

    /**
     * Add My location pin to the Map with radius and accuracy circle
     *
     * @param location
     * @param radius
     */
    private void addMyLocationAndRadius(Location location, int radius) {
        LatLng coord = new LatLng(location.getLatitude(), location.getLongitude());
        addMyLocation(coord);
        Resources r = getResources();
        addCircle(coord, radius, r.getColor(R.color.map_radius_stroke),
                r.getColor(R.color.map_radius_fill));
        addCircle(coord, (int) location.getAccuracy(), r.getColor(R.color.map_accuracy_stroke),
                r.getColor(R.color.map_accuracy_fill));
    }

    /**
     * Remove all pins and update mapview
     */
    private void updateMapPins() {
        map.clear();
        Location location = lm.getLocation();
        if (location != null) {
            addMyLocationAndRadius(location, taskRadius);
        }
        moveMapCameraToBoundsAndInitClusterkraf();
    }

    private void setRaiusText() {
        txtRadius.setText("" + taskRadius + " m");
    }

    /**
     * Move camera to current location or show Toast message if location not defined.
     */
    private void moveCameraToMyLocation() {
        L.d(TAG, "moveCameraToMyLocation()");
        if (restoreCameraPosition != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(restoreCameraPosition));
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined, Toast.LENGTH_LONG);
        }
    }
}
