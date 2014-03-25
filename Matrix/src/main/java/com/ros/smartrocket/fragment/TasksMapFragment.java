package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
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
import com.ros.smartrocket.utils.UIUtils;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.Clusterkraf;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.OnInfoWindowClickDownstreamListener;
import com.twotoasters.clusterkraf.OnMarkerClickDownstreamListener;
import com.twotoasters.clusterkraf.Options;

import java.util.ArrayList;
import java.util.Locale;

public class TasksMapFragment extends Fragment implements NetworkOperationListenerInterface, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String TAG = TasksMapFragment.class.getSimpleName();
    private static final String MYLOC = "MyLoc";
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private ImageView btnFilter;
    private LinearLayout rlFilterPanel;
    private ToggleButton showHiddenTasksToggleButton;
    private boolean isFilterShow = false;
    private GoogleMap map;
    private CameraPosition restoreCameraPosition;
    private static final int DEFAULT_TASK_RADIUS = 5000;
    private static final int METERS_IN_KM = 1000;
    public static int taskRadius = DEFAULT_TASK_RADIUS;
    private int sbRadiusProgress = 5;
    private static final int RADIUS_DELTA = 1000; // 1% = 1000m => Max = 100km
    private TextView txtRadius;
    private static final float DEFAULT_ZOOM_LEVEL = 11f;
    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private SeekBar sbRadius;
    private ImageView refreshButton;
    private MarkerOptions myPinLocation;

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

        if (this.options == null) {
            this.options = new ClusterOptions();
        }

        handler = new DbHandler(getActivity().getContentResolver());

        btnFilter = (ImageView) view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(this);
        view.findViewById(R.id.btnMyLocation).setOnClickListener(this);
        view.findViewById(R.id.applyButton).setOnClickListener(this);
        showHiddenTasksToggleButton = (ToggleButton) view.findViewById(R.id.showHiddenTasksToggleButton);
        showHiddenTasksToggleButton.setOnCheckedChangeListener(this);

        rlFilterPanel = (LinearLayout) view.findViewById(R.id.hidden_panel);
        sbRadius = (SeekBar) rlFilterPanel.findViewById(R.id.seekBarRadius);
        txtRadius = (TextView) rlFilterPanel.findViewById(R.id.txtRadius);
        taskRadius = DEFAULT_TASK_RADIUS;

        setRadiusText();

        sbRadius.setProgress(sbRadiusProgress);
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sbRadiusProgress = progress;
                taskRadius = RADIUS_DELTA * sbRadiusProgress;
                setRadiusText();
                updateMapPins(lm.getLocation());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                TasksBL.getNotMyTasksFromDBbyRadius(handler, taskRadius, showHiddenTasksToggleButton.isChecked());
                Location location = lm.getLocation();
                if (location == null) {
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

        if (clusterkraf != null) {
            clusterkraf.clear();
        }
        //Remove my location and Circle from the map!
        map.clear();

        setViewMode(getArguments());
        updateUI();
        loadData();

        showFilterPanel(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (clusterkraf != null) {
                clusterkraf.clear();
            }
            //Remove my location and Circle from the map!
            map.clear();


            setViewMode(getArguments());
            updateUI();
            loadData();
        }
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
        if ((mode == Keys.MapViewMode.SURVEYTASKS) || (mode == Keys.MapViewMode.SINGLETASK)) {
            viewItemId = bundle.getInt(Keys.MAP_VIEWITEM_ID);
        }
    }


    /**
     * Get Tasks from local db
     */
    private void loadTasksFromLocalDb() {
        if (mode == Keys.MapViewMode.ALLTASKS) {
            TasksBL.getNotMyTasksFromDBbyRadius(handler, taskRadius, showHiddenTasksToggleButton.isChecked());
        } else if (mode == Keys.MapViewMode.MYTASKS) {
            TasksBL.getMyTasksFromDB(handler);
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
        if (mode == Keys.MapViewMode.MYTASKS) {
            getMyTasksFromServer();
        } else if (mode == Keys.MapViewMode.ALLTASKS) {
            getSurveysFromServer(location, taskRadius);
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
                    } else {
                        UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined);
                    }
                }
            });
        }
    }

    private void loadData() {
        refreshIconState(true);
        Location location = lm.getLocation();
        if (location != null) {
            loadTasksFromLocalDb();
            updateDataFromServer(location);
            addMyLocationAndRadius(location, taskRadius);
            if (isFirstStart) {
                moveCameraToMyLocation();
                isFirstStart = false;
            }
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.looking_for_location);
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    L.i(TAG, "Location Updated!");
                    loadTasksFromLocalDb();
                    updateDataFromServer(location);
                    addMyLocationAndRadius(location, taskRadius);
                    if (isFirstStart) {
                        moveCameraToMyLocation();
                        isFirstStart = false;
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
            if (Keys.GET_SURVEYS_OPERATION_TAG.equals(operation.getTag()) ||
                    Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
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
        for (int i = 0; i < list.size(); i++) {
            Task item = list.get(i);
            if (item.getLatitude() != null && item.getLongitude() != null) {
                inputPoints.add(new InputPoint(item.getLatLng(), item));
            } else {
                item.setLatitude(location.getLatitude() - (i == 0 ? 0.000040f : 0.000040f * (i + 1)));
                item.setLongitude(location.getLongitude() + (i == 0 ? 0.000040f : 0.000040f * (i + 1)));

                inputPoints.add(new InputPoint(item.getLatLng(), item));
            }
        }

        Log.i(TAG, "[tasks.size=" + inputPoints.size() + "]");
        Log.i(TAG, "initClusterkraf [tasks.size=" + inputPoints.size() + "]");

        if (inputPoints.size() > 0) {
            if (clusterkraf == null) {
                initClusterkraf(inputPoints);
            } else {
                clusterkraf.replace(inputPoints);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMyLocation:
                moveCameraToMyLocation();
                break;
            case R.id.applyButton:
                loadData();
                toggleFilterPanel();
                break;
            case R.id.btnFilter:
                toggleFilterPanel();
                break;
            case R.id.refreshButton:
                loadData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.showHiddenTasksToggleButton:
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
        refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);

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
            return mode == Keys.MapViewMode.SURVEYTASKS || mode == Keys.MapViewMode.SINGLETASK;
        }
    };

    private OnInfoWindowClickDownstreamListener onInfoWindowClickListener = new OnInfoWindowClickDownstreamListener() {
        @Override
        public boolean onInfoWindowClick(Marker marker, ClusterPoint clusterPoint) {
            int taskId = Integer.valueOf(marker.getSnippet());
            startActivity(IntentUtils.getTaskDetailIntent(getActivity(), taskId));
            return false;
        }
    };

    /**
     * Settings for Cluster library
     */
    private static class ClusterOptions {
        private static final int TRANSITION_DURATION = 500;
        //private String transitionInterpolator = LinearInterpolator.class.getCanonicalName();
        private static final int DIP_DISTANCE_TO_JOIN_CLUSTER = 100;
        private static final int ZOOM_TO_BOUNDS_ANIMATION_DURATION = 500;
        private static final int SHOW_INFO_WINDOW_ANIMATION_DURATION = 500;
        private static final double EXPAND_BOUNDS_FACTOR = 0.5d;
        private com.twotoasters.clusterkraf.Options.SinglePointClickBehavior singlePointClickBehavior = com
                .twotoasters.clusterkraf.Options.SinglePointClickBehavior.SHOW_INFO_WINDOW;
        private Options.ClusterClickBehavior clusterClickBehavior = Options.ClusterClickBehavior.ZOOM_TO_BOUNDS;
        private Options.ClusterInfoWindowClickBehavior clusterInfoWindowClickBehavior = Options.
                ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS;
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

    /**
     * Draw circle on the map
     *
     * @param coordinates
     * @param radius
     */
    private void addCircle(LatLng coordinates, int radius, int strokeColor, int fillColor) {
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
        restoreCameraPosition = new CameraPosition(coordinates, zoomLevel, 0, 0);
    }

    /**
     * Add My location pin to the Map with radius and accuracy circle
     *
     * @param location - should be not null
     * @param radius
     */
    private void addMyLocationAndRadius(Location location, int radius) {
        if (location != null) {
            LatLng coord = new LatLng(location.getLatitude(), location.getLongitude());

            addMyLocation(coord);

            if (getActivity() != null) {
                Resources r = getActivity().getResources();
                addCircle(coord, radius, r.getColor(R.color.map_radius_stroke),
                        r.getColor(R.color.map_radius_fill));
                addCircle(coord, (int) location.getAccuracy(), r.getColor(R.color.map_accuracy_stroke),
                        r.getColor(R.color.map_accuracy_fill));
            }
        }
    }

    /**
     * Remove all pins and update mapview
     */
    private void updateMapPins(Location location) {
        map.clear();
        addMyLocationAndRadius(location, taskRadius);
    }

    private void setRadiusText() {
        String distance = String.format(Locale.US, "%.1f", (float) taskRadius / METERS_IN_KM);
        txtRadius.setText(distance + " km");
    }

    /**
     * Move camera to current location or show Toast message if location not defined.
     */
    private void moveCameraToMyLocation() {
        if (restoreCameraPosition != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(restoreCameraPosition));
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined, Toast.LENGTH_LONG);
        }
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
