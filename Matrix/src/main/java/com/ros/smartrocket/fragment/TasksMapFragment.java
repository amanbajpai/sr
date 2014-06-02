package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.adapter.CustomInfoMapWindowAdapter;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.map.TaskOptionsChooser;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.Clusterkraf;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.OnInfoWindowClickDownstreamListener;
import com.twotoasters.clusterkraf.OnMarkerClickDownstreamListener;
import com.twotoasters.clusterkraf.Options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TasksMapFragment extends Fragment implements NetworkOperationListenerInterface, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String TAG = TasksMapFragment.class.getSimpleName();
    private static final String MYLOC = "MyLoc";
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private ImageView btnFilter;
    private LinearLayout rlFilterPanel;
    private ToggleButton showHiddenTasksToggleButton;
    private boolean isFilterShow = false;
    private GoogleMap map;
    private CameraPosition restoreCameraByPositionAndRadius;
    private LatLngBounds restoreCameraByPins;
    private static final float COORDINATE_OFFSET = 0.00004f;
    private static final int METERS_IN_KM = 1000;
    public static int DEFAULT_TASK_RADIUSE = 5000;
    public static int taskRadius = DEFAULT_TASK_RADIUSE;
    private int sbRadiusProgress = 5;
    private static final int RADIUS_DELTA = 200; // 1% = 200m => Max = 20km
    private TextView txtRadius;
    private static final float DEFAULT_ZOOM_LEVEL = 11f;
    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private SeekBar sbRadius;
    private ImageView refreshButton;
    private MarkerOptions myPinLocation;
    private HashMap<String, String> markerLocation = new HashMap<String, String>();

    private Display display;
    private float mapWidth;
    private Clusterkraf clusterkraf;
    private ClusterOptions options;

    private AsyncQueryHandler handler;
    private Keys.MapViewMode mode;
    private int viewItemId = 0; // Used for Survey and SingleTask map mode view
    private boolean isFirstStart = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_map, null);

        display = getActivity().getWindowManager().getDefaultDisplay();
        mapWidth = UIUtils.getDpFromPx(getActivity(), display.getWidth() - 60);

        if (this.options == null) {
            this.options = new ClusterOptions();
        }

        handler = new DbHandler(getActivity().getContentResolver());

        btnFilter = (ImageView) view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(this);

        view.findViewById(R.id.btnMyLocation).setOnClickListener(this);
        view.findViewById(R.id.applyButton).setOnClickListener(this);
        showHiddenTasksToggleButton = (ToggleButton) view.findViewById(R.id.showHiddenTasksToggleButton);
        showHiddenTasksToggleButton.setChecked(preferencesManager.getShowHiddenTask());
        showHiddenTasksToggleButton.setOnCheckedChangeListener(this);

        rlFilterPanel = (LinearLayout) view.findViewById(R.id.hidden_panel);
        sbRadius = (SeekBar) rlFilterPanel.findViewById(R.id.seekBarRadius);
        txtRadius = (TextView) rlFilterPanel.findViewById(R.id.txtRadius);

        taskRadius = preferencesManager.getDefaultRadius();
        sbRadiusProgress = taskRadius / RADIUS_DELTA;

        setRadiusText();

        sbRadius.setProgress(sbRadiusProgress);
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sbRadiusProgress = progress;
                taskRadius = RADIUS_DELTA * sbRadiusProgress;
                setRadiusText();

                Location location = lm.getLocation();
                if (location != null && UIUtils.isGpsEnabled(getActivity())) {
                    map.clear();
                    addMyLocation(location);
                    addRadiusAndZoomByRadius(location);
                    moveCameraToLocation();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                preferencesManager.setDefaultRadius(taskRadius);
                loadTasksFromLocalDb();
                Location location = lm.getLocation();
                if (location == null && UIUtils.isOnline(getActivity()) && UIUtils.isGpsEnabled(getActivity())) {
                    UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();

        setViewMode(getArguments());
        updateUI();
        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            setViewMode(getArguments());
            updateUI();
            loadData();
        } else {
            if (isFilterShow) {
                showFilterPanel(false);
            }
        }
    }

    /**
     * Initialize Google Map
     */
    private void initMap() {
        if (map == null) {
            TransparentSupportMapFragment mapFragment = (TransparentSupportMapFragment) getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.map);
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
                        }
                    });
                }
            }
        }
    }

    /**
     * Inirialize Cluster library and add pins
     */
    private void initClusterkraf(ArrayList<InputPoint> inputPoints) {
        if (this.map != null && inputPoints != null && inputPoints.size() > 0) {
            com.twotoasters.clusterkraf.Options options = new com.twotoasters.clusterkraf.Options();
            applyemoApplicationOptionsToClusterkrafOptions(options);
            // customize the options before you construct a Clusterkraf instance
            this.clusterkraf = new Clusterkraf(this.map, options, inputPoints);
        }
    }

    public void clearMap() {
        if (clusterkraf != null) {
            clusterkraf.clear();
        }
        //Remove my location and Circle from the map!
        map.clear();
    }

    /**
     * Get View mode type from Intent
     *
     * @param bundle - fragment arguments
     */
    private void setViewMode(Bundle bundle) {
        if (bundle != null) {
            mode = Keys.MapViewMode.valueOf(bundle.getString(Keys.MAP_MODE_VIEWTYPE));
        }
        Log.i(TAG, "setViewMode() [mode  =  " + mode + "]");
        btnFilter.setVisibility(mode == Keys.MapViewMode.ALLTASKS ? View.VISIBLE : View.INVISIBLE);

        if ((mode == Keys.MapViewMode.SURVEYTASKS) || (mode == Keys.MapViewMode.SINGLETASK)) {
            viewItemId = bundle.getInt(Keys.MAP_VIEWITEM_ID);
        }
    }

    private void toggleFilterPanel() {
        showFilterPanel(!isFilterShow);
    }

    private void showFilterPanel(boolean show) {
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
     * Update All UI elements when view mode change
     */
    private void updateUI() {
        if (mode == Keys.MapViewMode.MYTASKS
                || mode == Keys.MapViewMode.SURVEYTASKS) {
            btnFilter.setEnabled(false);
        } else {
            btnFilter.setEnabled(true);
        }
    }

    private void loadData() {
        refreshIconState(true);
        clearMap();
        Location location = lm.getLocation();
        if (location != null) {
            loadTasksFromLocalDb();
            updateDataFromServer(location);
        } else if (UIUtils.isGpsEnabled(getActivity())) {
            UIUtils.showSimpleToast(getActivity(), R.string.looking_for_location);
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    L.i(TAG, "Location Updated!");
                    loadTasksFromLocalDb();
                    updateDataFromServer(location);
                }
            });
        } else {
            refreshIconState(false);
            loadTasksFromLocalDb();
            updateDataFromServer(location);
        }
    }

    /**
     * Get Tasks from local db
     */
    private void loadTasksFromLocalDb() {
        if (mode == Keys.MapViewMode.ALLTASKS) {
            TasksBL.getAllNotMyTasksFromDB(handler, showHiddenTasksToggleButton.isChecked());
        } else if (mode == Keys.MapViewMode.MYTASKS) {
            TasksBL.getMyTasksForMapFromDB(handler);
        } else if (mode == Keys.MapViewMode.SURVEYTASKS) {
            TasksBL.getNotMyTasksFromDBbySurveyId(handler, viewItemId, showHiddenTasksToggleButton.isChecked());
            Log.d(TAG, "loadTasksFromLocalDb() [surveyId  =  " + viewItemId + "]");
        } else if (mode == Keys.MapViewMode.SINGLETASK) {
            TasksBL.getTaskFromDBbyID(handler, viewItemId);
            Log.d(TAG, "loadTasksFromLocalDb() [taskId  =  " + viewItemId + "]");
        }

        Log.i(TAG, "loadTasksFromLocalDb() [mode  =  " + mode + "]");

    }


    /**
     * Send request to server for data update
     */
    private void updateDataFromServer(Location location) {
        if (UIUtils.isOnline(getActivity())) {
            if (mode == Keys.MapViewMode.MYTASKS) {
                getMyTasksFromServer();
            } else if (mode == Keys.MapViewMode.ALLTASKS) {
                getSurveysFromServer(location, taskRadius);
            }
        } else {
            refreshIconState(false);
            UIUtils.showSimpleToast(getActivity(), R.string.no_internet);
        }
    }

    /**
     * Initiate call to server side and get my Tasks
     */
    private void getMyTasksFromServer() {
        ((BaseActivity) getActivity()).sendNetworkOperation(APIFacade.getInstance().getMyTasksOperation());
    }

    /**
     * Initiate call to server side and get Tasks
     *
     * @param radius - selected radius
     */
    private void getSurveysFromServer(final Location location, final int radius) {
        if (location != null) {
            lm.getAddress(location, new MatrixLocationManager.IAddress() {
                @Override
                public void onUpdate(Address address) {
                    if (address != null) {
                        APIFacade.getInstance().getSurveys(getActivity(), location.getLatitude(),
                                location.getLongitude(), address.getCountryName(), address.getLocality(), radius);
                    } else if (UIUtils.isOnline(getActivity())) {
                        UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined);
                    }
                }
            });
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
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    ArrayList<Task> tasks = TasksBL.convertCursorToTasksList(cursor);
                    onLoadingComplete(tasks);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_SURVEYS_OPERATION_TAG.equals(operation.getTag())
                    || Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
                loadTasksFromLocalDb();
            }
        } else {
            L.i(TAG, operation.getResponseError());
        }
        refreshIconState(false);
    }

    /**
     * Callback when we finish loading task from Server
     *
     * @param list - result list with data
     */
    private void onLoadingComplete(ArrayList<Task> list) {
        ArrayList<InputPoint> inputPoints = new ArrayList<InputPoint>();
        Location location = lm.getLocation();

        markerLocation.clear();

        for (int i = 0; i < list.size(); i++) {
            Task item = list.get(i);
            Double latitude = item.getLatitude();
            Double longitude = item.getLongitude();

            if (location != null && (latitude == null || longitude == null)) {
                latitude = location.getLatitude() + COORDINATE_OFFSET;
                longitude = location.getLongitude() + COORDINATE_OFFSET;
            }

            if (latitude != null && longitude != null) {
                Double[] newTaskCoordinate = coordinateForMarker(list.size(), latitude, longitude);
                if (newTaskCoordinate != null) {
                    item.setLatitude(newTaskCoordinate[0]);
                    item.setLongitude(newTaskCoordinate[1]);
                }
                inputPoints.add(new InputPoint(item.getLatLng(), item));
            }
        }

        Log.i(TAG, "[tasks.size=" + inputPoints.size() + "]");

        if (inputPoints.size() > 0) {
            if (clusterkraf == null) {
                initClusterkraf(inputPoints);
            } else {
                clusterkraf.replace(inputPoints);
            }
        }

        if (mode == Keys.MapViewMode.ALLTASKS) {
            addRadiusAndZoomByRadius(location);
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (InputPoint point : inputPoints) {
                builder.include(point.getMapPosition());
            }
            builder.include(new LatLng(location.getLatitude(), location.getLongitude()));

            restoreCameraByPins = inputPoints.size() > 0 ? builder.build() : null;
        }


        if (isFirstStart) {
            moveCameraToLocation();
            isFirstStart = false;
        }

        addMyLocation(location);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMyLocation:
                moveCameraToLocation();
                break;
            case R.id.applyButton:
                //loadData();
                toggleFilterPanel();
                break;
            case R.id.btnFilter:
                toggleFilterPanel();
                break;
            case R.id.refreshButton:
                loadData();
                IntentUtils.refreshProfileAndMainMenu(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.showHiddenTasksToggleButton:
                preferencesManager.setShowHiddenTask(isChecked);
                loadData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();

        View view = actionBar.getCustomView();
        if (view != null) {
            refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
            if (refreshButton != null) {
                refreshButton.setOnClickListener(this);
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshIconState(boolean isLoading) {
        if (refreshButton != null && getActivity() != null) {
            if (isLoading) {
                refreshButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
            } else {
                refreshButton.clearAnimation();
            }
        }
    }


    /* ==============================================
    * Methods for Clusters pins display on the map
    * ============================================== */


    /**
     * Applies the sample.SampleActivity.Options chosen in Normal or Advanced
     * mode menus to the clusterkraf.Options which will be used to construct our
     * Clusterkraf instance
     *
     * @param options
     */
    private void applyemoApplicationOptionsToClusterkrafOptions(com.twotoasters.clusterkraf.Options options) {
        options.setTransitionDuration(this.options.TRANSITION_DURATION);

        /* Hardcoded Transaction type to avoid */
        options.setTransitionInterpolator(new LinearInterpolator());

        options.setPixelDistanceToJoinCluster(getPixelDistanceToJoinCluster());
        options.setZoomToBoundsAnimationDuration(this.options.ZOOM_TO_BOUNDS_ANIMATION_DURATION);
        options.setShowInfoWindowAnimationDuration(this.options.SHOW_INFO_WINDOW_ANIMATION_DURATION);
        options.setExpandBoundsFactor(this.options.EXPAND_BOUNDS_FACTOR);
        options.setSinglePointClickBehavior(this.options.singlePointClickBehavior);
        options.setClusterClickBehavior(this.options.clusterClickBehavior);
        options.setClusterInfoWindowClickBehavior(this.options.clusterInfoWindowClickBehavior);

        /*Live hack from library developers ^)*/
        options.setZoomToBoundsPadding(getResources().getDrawable(R.drawable.ic_map_cluster_pin).getIntrinsicHeight());

        options.setMarkerOptionsChooser(new TaskOptionsChooser(getActivity()));

        options.setOnMarkerClickDownstreamListener(onMarkerClickListener);


        options.setOnInfoWindowClickDownstreamListener(onInfoWindowClickListener);
        options.setInfoWindowDownstreamAdapter(new CustomInfoMapWindowAdapter(getActivity(), mode));
        //options.setProcessingListener(this);
    }

    /**
     * Help util method to get px for Cluster icon
     *
     * @return
     */
    private int getPixelDistanceToJoinCluster() {
        return UIUtils.getPxFromDp(getActivity(), this.options.DIP_DISTANCE_TO_JOIN_CLUSTER);
    }

    private OnMarkerClickDownstreamListener onMarkerClickListener = new OnMarkerClickDownstreamListener() {
        @Override
        public boolean onMarkerClick(Marker marker, ClusterPoint clusterPoint) {
            return mode == Keys.MapViewMode.SINGLETASK;
        }
    };

    private OnInfoWindowClickDownstreamListener onInfoWindowClickListener = new OnInfoWindowClickDownstreamListener() {
        @Override
        public boolean onInfoWindowClick(Marker marker, ClusterPoint clusterPoint) {
            String[] taskData = marker.getSnippet().split("_");
            int taskId = Integer.valueOf(taskData[0]);
            int surveyId = Integer.valueOf(taskData[1]);
            int taskStatusId = Integer.valueOf(taskData[2]);

            switch (TasksBL.getTaskStatusType(taskStatusId)) {
                case none:
                case claimed:
                case started:
                    startActivity(IntentUtils.getTaskDetailIntent(getActivity(), taskId));
                    break;
                case scheduled:
                    startActivity(IntentUtils.getTaskValidationIntent(getActivity(), taskId, false));
                    break;
                case reDoTask:
                    startActivity(IntentUtils.getQuestionsIntent(getActivity(), surveyId, taskId));
                    break;
                default:
                    return true;
            }

            return false;
        }
    };

    /**
     * Settings for Cluster library
     */
    private static class ClusterOptions {
        private static final int TRANSITION_DURATION = 500;
        //private String transitionInterpolator = LinearInterpolator.class.getCanonicalName();
        private static final int DIP_DISTANCE_TO_JOIN_CLUSTER = 50;
        private static final int ZOOM_TO_BOUNDS_ANIMATION_DURATION = 500;
        private static final int SHOW_INFO_WINDOW_ANIMATION_DURATION = 500;
        private static final double EXPAND_BOUNDS_FACTOR = 0.5d;
        private com.twotoasters.clusterkraf.Options.SinglePointClickBehavior singlePointClickBehavior = com
                .twotoasters.clusterkraf.Options.SinglePointClickBehavior.SHOW_INFO_WINDOW;
        private Options.ClusterClickBehavior clusterClickBehavior = Options.ClusterClickBehavior.ZOOM_TO_BOUNDS;
        private Options.ClusterInfoWindowClickBehavior clusterInfoWindowClickBehavior = Options.
                ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS;
    }

    private void restoreCameraPositionByRadius(Location location, int radius) {
        if (location != null) {
            zoomLevel = getZoomForMetersWide(radius * 2, location.getLatitude());

            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            restoreCameraByPositionAndRadius = new CameraPosition(coordinates, zoomLevel, 0, 0);
        }
    }

    public float getZoomForMetersWide(final double desiredMeters, final double latitude) {
        final double latitudinalAdjustment = Math.cos(Math.PI * latitude / 180.0);

        final double arg = (40075004 * mapWidth * latitudinalAdjustment / (desiredMeters * 256.0));

        return (float) (Math.log(arg) / Math.log(2.0));
    }

    /**
     * Add marker with myu location on the map
     *
     * @param location
     */
    private void addMyLocation(Location location) {
        L.d(TAG, "addMyLocation");
        if (location != null) {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions()
                    .snippet(MYLOC)
                    .position(coordinates)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)));
        }
    }

    /**
     * Add Radius and zoom map by radius
     *
     * @param location - should be not null
     */
    private void addRadiusAndZoomByRadius(Location location) {
        if (location != null) {
            LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
            restoreCameraPositionByRadius(lm.getLocation(), taskRadius);

            if (getActivity() != null) {
                Resources r = getActivity().getResources();
                addCircle(coordinate, taskRadius, r.getColor(R.color.map_radius_stroke),
                        r.getColor(R.color.map_radius_fill));
                /*addCircle(coordinate, (int) location.getAccuracy(), r.getColor(R.color.map_accuracy_stroke),
                        r.getColor(R.color.map_accuracy_fill));*/
            }
        }
    }

    /**
     * Draw circle on the map
     *
     * @param coordinates - center coordinates
     * @param radius      - current radius
     */
    private void addCircle(LatLng coordinates, int radius, int strokeColor, int fillColor) {
        map.addCircle(new CircleOptions()
                .center(coordinates)
                .radius(radius)
                .strokeColor(strokeColor)
                .strokeWidth(5f)
                /*.fillColor(fillColor)*/);
    }

    private void setRadiusText() {
        String distance = String.format(Locale.US, "%.1f", (float) taskRadius / METERS_IN_KM);
        txtRadius.setText(distance + " km");
    }

    /**
     * Move camera to current location or show Toast message if location not defined.
     */
    private void moveCameraToLocation() {
        if (mode == Keys.MapViewMode.ALLTASKS) {
            if (restoreCameraByPositionAndRadius != null) {
                map.moveCamera(CameraUpdateFactory.newCameraPosition(restoreCameraByPositionAndRadius));
            } else if (UIUtils.isOnline(getActivity())) {
                UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined, Toast.LENGTH_LONG);
            }
        } else if (mode == Keys.MapViewMode.MYTASKS) {
            Location location = lm.getLocation();
            if (location != null) {
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            }
        } else {
            if (restoreCameraByPins != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(restoreCameraByPins, display.getWidth(),
                        display.getHeight(), 150));
            }
        }
    }

    /**
     * Check coordinates of pins and change it if they are equals
     */
    private Double[] coordinateForMarker(int itemSize, double latitude, double longitude) {

        Double[] location = null;
        String locationToAdd = null;

        for (int i = 0; i <= itemSize; i++) {

            if (mapAlreadyHasMarkerForLocation((latitude + i * COORDINATE_OFFSET)
                    + "," + (longitude + i * COORDINATE_OFFSET))) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                if (mapAlreadyHasMarkerForLocation((latitude - i * COORDINATE_OFFSET)
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
            markerLocation.put(locationToAdd, locationToAdd);
        }

        return location;
    }

    // Return whether marker with same location is already on map
    private boolean mapAlreadyHasMarkerForLocation(String location) {
        return (markerLocation.containsValue(location));
    }

    @Override
    public void onStart() {
        super.onStart();
        ((BaseActivity) getActivity()).addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        ((BaseActivity) getActivity()).removeNetworkOperationListener(this);
        super.onStop();
    }
}
