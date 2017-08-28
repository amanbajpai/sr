package com.ros.smartrocket.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Stroke;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.ui.base.BaseFragment;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.interfaces.SwitchCheckedChangeListener;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.map.MapHelper;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.ui.views.CustomSwitch;
import com.twotoasters.baiduclusterkraf.OnShowInfoWindowListener;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.Clusterkraf;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.OnInfoWindowClickDownstreamListener;
import com.twotoasters.clusterkraf.OnMarkerClickDownstreamListener;
import com.twotoasters.clusterkraf.Options;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TasksMapFragment extends BaseFragment implements NetworkOperationListenerInterface,
        View.OnClickListener, SwitchCheckedChangeListener,
        OnMarkerClickDownstreamListener, OnInfoWindowClickDownstreamListener {

    private static final String TAG = TasksMapFragment.class.getSimpleName();
    private static final String MY_LOCATION = "MyLoc";
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private ImageView btnFilter;
    private LinearLayout rlFilterPanel;
    private CustomSwitch showHiddenTasksToggleButton;
    private boolean isFilterShow = false;
    private GoogleMap googleMap;
    private BaiduMap baiduMap;
    private CameraPosition restoreCameraByPositionAndRadius;
    private LatLngBounds restoreCameraByPins;
    private com.baidu.mapapi.model.LatLngBounds restoreCameraByBaiduPins;
    private static final int METERS_IN_KM = 1000;
    public static int DEFAULT_TASK_RADIUS = 5000;
    public static int taskRadius = DEFAULT_TASK_RADIUS;
    private int sbRadiusProgress = 5;
    private boolean isTracking = false;
    // 1% = 200m => Max = 20km
    private static final int RADIUS_DELTA = 200;
    private TextView txtRadius;
    private static final float DEFAULT_ZOOM_LEVEL = 11f;
    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private ImageView roundImage;
    private ImageView refreshButton;

    private Display display;
    private float mapWidth;
    private Clusterkraf clusterkraf;
    private com.twotoasters.baiduclusterkraf.Clusterkraf baiduClusterkraf;

    private AsyncQueryHandler handler;
    private Keys.MapViewMode mode;

    // Used for Wave and SingleTask map mode view
    private int viewItemId = 0;

    private boolean isFirstStart = true;
    private Marker currentLocationMarker;
    private Circle circle;
    private Overlay circleBaidu;
    private boolean isNeedRefresh = true;
    private View hideMissionsLayout;
    private SeekBar seekBarRadius;

    public TasksMapFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_map, null);

        initRefreshButton();
        refreshIconState(true);

        LinearLayout mapLayout = (LinearLayout) view.findViewById(R.id.mapLayout);

        View mapView = mapLayout.findViewById(R.id.map);
        if (savedInstanceState == null && mapView == null) {
            try {
                if (Config.USE_BAIDU) {
                    mapLayout.addView(LayoutInflater.from(getActivity()).inflate(R.layout.fragment_baidu_map, null));
                } else {
                    mapLayout.addView(LayoutInflater.from(getActivity()).inflate(R.layout.fragment_google_map, null));
                }
            } catch (Exception e) {
                L.e(TAG, "Error in onCreateView method.", e);
                getActivity().finish();
            }
        }

        display = getActivity().getWindowManager().getDefaultDisplay();
        mapWidth = UIUtils.getDpFromPx(getActivity(),
                display.getWidth() - UIUtils.getPxFromDp(getActivity(), 20));

        handler = new DbHandler(getActivity().getContentResolver());

        roundImage = (ImageView) view.findViewById(R.id.roundImage);
        roundImage.setImageResource(Config.USE_BAIDU ? R.drawable.round_baidu : R.drawable.round);
        btnFilter = (ImageView) view.findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(this);
        hideMissionsLayout = view.findViewById(R.id.hideMissionsLayout);

        view.findViewById(R.id.btnMyLocation).setOnClickListener(this);
        view.findViewById(R.id.applyButton).setOnClickListener(this);
        showHiddenTasksToggleButton = (CustomSwitch) view.findViewById(R.id.showHiddenTasksToggleButton);
        showHiddenTasksToggleButton.setChecked(preferencesManager.getShowHiddenTask());
        showHiddenTasksToggleButton.setOnCheckedChangeListener(this);

        rlFilterPanel = (LinearLayout) view.findViewById(R.id.hidden_panel);
        seekBarRadius = (SeekBar) rlFilterPanel.findViewById(R.id.seekBarRadius);
        txtRadius = (TextView) rlFilterPanel.findViewById(R.id.txtRadius);
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking = true;
                clearMap();
                moveCameraToLocation();
                roundImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sbRadiusProgress = progress;
                taskRadius = RADIUS_DELTA * sbRadiusProgress;
                setRadiusText();

                restoreCameraPositionByRadius(lm.getLocation(), taskRadius);
                moveCameraToLocation();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTracking = false;
                preferencesManager.setDefaultRadius(taskRadius);
                roundImage.setVisibility(View.GONE);
                loadTasksFromLocalDb();

                Location location = lm.getLocation();

                clearMap();
                if (preferencesManager.getUseLocationServices() && lm.isConnected()) {
                    addMyLocation(location);
                    addRadius(location);

                    if (location == null && UIUtils.isOnline(getActivity())) {
                        UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined);
                    }
                }

            }
        });

        lm.setCurrentLocationUpdateListener(currentLocationUpdateListener);

        return view;
    }

    private void initTaskRadius() {
        taskRadius = preferencesManager.getDefaultRadius();
        sbRadiusProgress = taskRadius / RADIUS_DELTA;
        setRadiusText();
        seekBarRadius.setProgress(sbRadiusProgress);
    }

    @Override
    public void onResume() {
        super.onResume();
        initTaskRadius();
        if (!isHidden() && isNeedRefresh) {
            isNeedRefresh = false;
            initMap();
            setViewMode(getArguments());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (baiduMap != null) {
            lm.setZoomLevel(baiduMap.getMapStatus().zoom);
            lm.setLastBaiduPosition(baiduMap.getMapStatus().target);
        } else if (googleMap != null) {
            lm.setZoomLevel(googleMap.getCameraPosition().zoom);
            lm.setLastGooglePosition(googleMap.getCameraPosition().target);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            setViewMode(getArguments());
            loadData(false);

            showHiddenTasksToggleButton.setChecked(preferencesManager.getShowHiddenTask());
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
        if (!MapHelper.isMapNotNull(googleMap, baiduMap)) {
            if (Config.USE_BAIDU) {
                baiduMap = MapHelper.getBaiduMap(getActivity(), new BaiduMap.OnMapStatusChangeListener() {
                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus) {
                        // not needed
                    }

                    @Override
                    public void onMapStatusChange(MapStatus mapStatus) {
                        // not needed
                    }

                    @Override
                    public void onMapStatusChangeFinish(MapStatus mapStatus) {
                        zoomLevel = mapStatus.zoom;
                    }
                });
                loadData(true);
            } else {
                loadGoogleMap();
            }
        }
    }

    public void loadGoogleMap() {
        TransparentSupportMapFragment mapFragment =
                (TransparentSupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(gm -> {
                if (gm != null) {
                    googleMap = gm;
                    UiSettings uiSettings = googleMap.getUiSettings();
                    uiSettings.setAllGesturesEnabled(false);
                    uiSettings.setScrollGesturesEnabled(true);
                    uiSettings.setZoomGesturesEnabled(true);
                    uiSettings.setIndoorLevelPickerEnabled(false);
                    googleMap.setIndoorEnabled(false);
                    googleMap.setOnCameraChangeListener(cameraPosition -> zoomLevel = cameraPosition.zoom);
                    loadData(true);
                }
            });

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

            boolean showFilterButton = mode == Keys.MapViewMode.ALL_TASKS || mode == Keys.MapViewMode.WAVE_TASKS
                    /* && !Config.USE_BAIDU || mode == Keys.MapViewMode.WAVE_TASKS*/;
            btnFilter.setVisibility(showFilterButton ? View.VISIBLE : View.INVISIBLE);
            if (mode == Keys.MapViewMode.WAVE_TASKS) {
                hideMissionsLayout.setVisibility(View.GONE);
            }
            if (mode == Keys.MapViewMode.WAVE_TASKS || mode == Keys.MapViewMode.SINGLE_TASK) {
                viewItemId = bundle.getInt(Keys.MAP_VIEW_ITEM_ID);
            }
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
     * Get Tasks
     */
    private void loadData(boolean serverUpdate) {
        if (preferencesManager.getUseLocationServices() && lm.isConnected()) {
            AllTaskFragment.stopRefreshProgress = !serverUpdate;
            refreshIconState(true);
            loadTasksFromLocalDb();

            if (serverUpdate) {
                updateDataFromServer();
            }
        } else {
            refreshIconState(false);
        }
    }

    /**
     * Get Tasks from local db
     */
    private void loadTasksFromLocalDb() {
        Log.i(TAG, "loadTasksFromLocalDb() [mode  =  " + mode + "]");
        if (mode == Keys.MapViewMode.WAVE_TASKS || mode == Keys.MapViewMode.SINGLE_TASK) {
            if (getActivity() != null) {
                ((BaseActivity) getActivity()).showLoading(true);
            }
        }

        new android.os.Handler().postDelayed(() -> {
            if (getActivity() != null) {
                ((BaseActivity) getActivity()).hideLoading();
                if (mode == Keys.MapViewMode.ALL_TASKS && preferencesManager.getUseLocationServices()) {
                    Log.d(TAG, "getAllNotMyTasksFromDB [waveId  =  " + viewItemId + "]");
                    TasksBL.getAllNotMyTasksFromDB(handler, showHiddenTasksToggleButton.isChecked(), taskRadius);
                } else if (mode == Keys.MapViewMode.MY_TASKS) {
                    Log.d(TAG, "getMyTasksForMapFromDB [waveId  =  " + viewItemId + "]");
                    TasksBL.getMyTasksForMapFromDB(handler);
                } else if (mode == Keys.MapViewMode.WAVE_TASKS && preferencesManager.getUseLocationServices() && getActivity() != null) {
                    Log.d(TAG, "getNotMyTasksFromDBbyWaveId [waveId  =  " + viewItemId + "]");
                    TasksBL.getNotMyTasksFromDBbyWaveId(handler, viewItemId, showHiddenTasksToggleButton
                            .isChecked());
                } else if (mode == Keys.MapViewMode.SINGLE_TASK && preferencesManager.getUseLocationServices() && getActivity() != null) {
                    Log.d(TAG, "getTaskFromDBbyID [taskId  =  " + viewItemId + "]");
                    TasksBL.getTaskFromDBbyID(handler, viewItemId, 0);
                }

                Log.i(TAG, "RUN loadTasksFromLocalDb() [mode  =  " + mode + "]");
            }
        }, 1000);
    }

    /**
     * Send request to server for data update
     */
    private void updateDataFromServer() {
        if (UIUtils.isOnline(getActivity())) {
            if (mode == Keys.MapViewMode.MY_TASKS) {
                getMyTasksFromServer();
            } else if (mode == Keys.MapViewMode.ALL_TASKS) {
                getWavesFromServer(taskRadius);
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
    private void getWavesFromServer(final int radius) {
        MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
            @Override
            public void getLocationStart() {

            }

            @Override
            public void getLocationInProcess() {

            }

            @Override
            public void getLocationSuccess(final Location location) {
                if (isFirstStart) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            APIFacade.getInstance().getWaves(getActivity(), location.getLatitude(), location.getLongitude(), radius);
                        }
                    }, 1000);
                } else {
                    APIFacade.getInstance().getWaves(getActivity(), location.getLatitude(), location.getLongitude(), radius);
                }

            }

            @Override
            public void getLocationFail(String errorText) {
                UIUtils.showSimpleToast(App.getInstance(), errorText);
            }
        });
    }

    @Override
    public void onCheckedChange(CustomSwitch customSwitch, boolean isChecked) {
        preferencesManager.setShowHiddenTask(isChecked);
        loadData(true);
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
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        onLoadingComplete(TasksBL.convertCursorToTasksList(cursor));
                        if (AllTaskFragment.stopRefreshProgress) {
                            refreshIconState(false);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.GET_WAVES_OPERATION_TAG.equals(operation.getTag())
                || Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
            AllTaskFragment.stopRefreshProgress = true;
            loadTasksFromLocalDb();
        }
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.GET_WAVES_OPERATION_TAG.equals(operation.getTag())
                || Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
            if (operation.getResponseStatusCode() == BaseNetworkService.DEVICE_INTEERNAL_ERROR) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                L.i(TAG, operation.getResponseError());
                refreshIconState(false);
            }
        }
    }

    private void onLoadingComplete(final List<Task> list) {
        final Location location = lm.getLocation();

        clearMap();

        MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
            @Override
            public void useGoogleMap(GoogleMap googleMap) {
                ArrayList<InputPoint> inputPoints = MapHelper.getGoogleMapInputPointList(list, location);

                addGoogleMapPins(inputPoints);
                switch (mode) {
                    case ALL_TASKS:
                    case WAVE_TASKS:
                        restoreCameraPositionByRadius(location, taskRadius);
                        addRadius(location);
                        break;
                    case MY_TASKS:
                        restoreCameraPositionByPins(location, inputPoints);
                        break;
                    default:
                        restoreCameraPositionByPins(location, inputPoints);
                        moveCameraToLocation();
                        break;
                }
            }

            @Override
            public void useBaiduMap(final BaiduMap baiduMap) {
                ArrayList<com.twotoasters.baiduclusterkraf.InputPoint> inputPoints = MapHelper.getBaiduMapInputPointList(list, location);

                addBaiduMapPins(inputPoints);

                switch (mode) {
                    case ALL_TASKS:
                    case WAVE_TASKS:
                        restoreCameraPositionByRadius(location, taskRadius);
                        addRadius(location);
                        break;
                    case MY_TASKS:
                        restoreCameraPositionByBaiduPins(location, inputPoints);
                        break;
                    default:
                        restoreCameraPositionByBaiduPins(location, inputPoints);
                        moveCameraToLocation();
                        break;
                }
            }
        });
        if (mode != Keys.MapViewMode.SINGLE_TASK) {
            if (lm.isLastLocationSaved()) {
                zoomLevel = lm.getZoomLevel();
                MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
                    @Override
                    public void useGoogleMap(GoogleMap googleMap) {
                        if (lm.getLastGooglePosition() != null) {
                            googleMap.moveCamera(
                                    CameraUpdateFactory.newCameraPosition(new CameraPosition(lm.getLastGooglePosition(), zoomLevel, 0, 0)));
                        }
                    }

                    @Override
                    public void useBaiduMap(BaiduMap baiduMap) {
                        if (baiduMap != null && getActivity() != null && !getActivity().isFinishing() && lm.getLastBaiduPositionPosition() != null) {
                            baiduMap.setMapStatus(
                                    MapStatusUpdateFactory.newLatLngZoom(lm.getLastBaiduPositionPosition(), lm.getZoomLevel() + 1.4f));
                        }
                    }
                });
            } else if (isFirstStart) {
                moveCameraToLocation();
            }
        }
        isFirstStart = false;

        if (preferencesManager.getUseLocationServices()) {
            addMyLocation(location);
        }
    }

    public void addGoogleMapPins(final ArrayList<InputPoint> inputPoints) {
        if (clusterkraf == null) {
            Options options = MapHelper.getGoogleClusterkrafOptions(getActivity(), mode,
                    TasksMapFragment.this, TasksMapFragment.this);
            clusterkraf = new Clusterkraf(googleMap, options, inputPoints);
        } else {
            clusterkraf.replace(inputPoints);
        }
    }

    public void addBaiduMapPins(final ArrayList<com.twotoasters.baiduclusterkraf.InputPoint> inputPoints) {
        if (baiduClusterkraf == null) {
            com.twotoasters.baiduclusterkraf.Options options = MapHelper.getBaiduClusterkrafOptions(getActivity(), mode,
                    onShowInfoWindowListener,
                    onMarkerClickDownstreamListener);

            baiduClusterkraf = new com.twotoasters.baiduclusterkraf.Clusterkraf(baiduMap, options, inputPoints);
        } else {
            baiduClusterkraf.replace(inputPoints);
        }
    }

    OnShowInfoWindowListener onShowInfoWindowListener = new OnShowInfoWindowListener() {
        @Override
        public boolean onShowInfoWindow(com.baidu.mapapi.map.Marker marker, com.twotoasters.baiduclusterkraf.ClusterPoint clusterPoint) {
            final Task task = (Task) marker.getExtraInfo().getSerializable(Keys.TASK);
            //TODO Get data from local DB
            //final Task updatedTask = TasksBL.convertCursorToTaskOrNull(TasksBL.getTaskFromDBbyID(task.getId()));

            if (task == null) {
                return false;
            }

            View overlayView = LayoutInflater.from(getActivity()).inflate(R.layout.map_info_window, null);

            MapHelper.setMapOverlayView(getActivity(), overlayView, task);

            InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                public void onInfoWindowClick() {
                    MapHelper.mapOverlayClickResult(getActivity(), task.getId(), task.getMissionId(),
                            task.getStatusId());
                    baiduMap.hideInfoWindow();
                }
            };

            final com.baidu.mapapi.model.LatLng ll = marker.getPosition();
            Point p = baiduMap.getProjection().toScreenLocation(ll);
            p.y -= 60;

            com.baidu.mapapi.model.LatLng llInfo = baiduMap.getProjection().fromScreenLocation(p);
            InfoWindow mInfoWindow = new InfoWindow(com.baidu.mapapi.map.BitmapDescriptorFactory.fromView(overlayView),
                    llInfo, 0, listener);
            baiduMap.showInfoWindow(mInfoWindow);

            return true;
        }
    };

    com.twotoasters.baiduclusterkraf.OnMarkerClickDownstreamListener onMarkerClickDownstreamListener = new com.twotoasters.baiduclusterkraf.OnMarkerClickDownstreamListener() {
        @Override
        public boolean onMarkerClick(com.baidu.mapapi.map.Marker marker, com.twotoasters.baiduclusterkraf.ClusterPoint clusterPoint) {

            return false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMyLocation:
                moveCameraToLocation();
                break;
            case R.id.applyButton:
                toggleFilterPanel();
                loadData(true);
                break;
            case R.id.btnFilter:
                toggleFilterPanel();
                break;
            case R.id.refreshButton:
                loadData(true);
                IntentUtils.refreshProfileAndMainMenu(getActivity());
                IntentUtils.refreshMainMenuMyTaskCount(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        initRefreshButton();

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initRefreshButton() {
        if (refreshButton == null) {
            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            View view = actionBar.getCustomView();
            if (view != null) {
                refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
                if (refreshButton != null) {
                    refreshButton.setOnClickListener(this);
                }
            }
        }
    }

    /* ==============================================
    * Methods for Clusters pins display on the map
    * ============================================== */

    @Override
    public boolean onMarkerClick(Marker marker, ClusterPoint clusterPoint) {
        return mode == Keys.MapViewMode.SINGLE_TASK;
    }

    @Override
    public boolean onInfoWindowClick(Marker marker, ClusterPoint clusterPoint) {
        if (marker != null) {
            try {
                String[] taskData = marker.getSnippet().split("_");
                int taskId = Integer.valueOf(taskData[0]);
                //int waveId = Integer.valueOf(taskData[1]);
                int taskStatusId = Integer.valueOf(taskData[2]);
                int missionId = Integer.valueOf(taskData[3]);

                MapHelper.mapOverlayClickResult(getActivity(), taskId, missionId, taskStatusId);
            } catch (Exception e) {
                L.e(TAG, "Error info vindow click" + e, e);
            }
        }
        return false;
    }

    private void restoreCameraPositionByPins(Location location, ArrayList<InputPoint> inputPoints) {
        if (location != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (InputPoint point : inputPoints) {
                builder.include(point.getMapPosition());
            }
            builder.include(new LatLng(location.getLatitude(), location.getLongitude()));

            restoreCameraByPins = /*!inputPoints.isEmpty() ?*/ builder.build() /*: null*/;
        }
    }

    private void restoreCameraPositionByBaiduPins(Location location, ArrayList<com.twotoasters.baiduclusterkraf.InputPoint> inputPoints) {
        if (location != null) {
            com.baidu.mapapi.model.LatLngBounds.Builder builder = new com.baidu.mapapi.model.LatLngBounds.Builder();
            for (com.twotoasters.baiduclusterkraf.InputPoint point : inputPoints) {
                builder.include(point.getMapPosition());
            }
            builder.include(new com.baidu.mapapi.model.LatLng(location.getLatitude(), location.getLongitude()));

            restoreCameraByBaiduPins = /*!inputPoints.isEmpty() ?*/ builder.build() /*: null*/;
        }
    }

    private void restoreCameraPositionByRadius(Location location, int radius) {
        if (location != null) {
            zoomLevel = MapHelper.getZoomForMetersWide(mapWidth, radius * 2, location.getLatitude());

            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            restoreCameraByPositionAndRadius = new CameraPosition(coordinates, zoomLevel, 0, 0);
        }
    }


    /**
     * Move camera to current location or show Toast message if location not defined.
     */
    private void moveCameraToLocation() {
        if (mode == Keys.MapViewMode.ALL_TASKS || mode == Keys.MapViewMode.WAVE_TASKS) {
            MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
                @Override
                public void useGoogleMap(GoogleMap googleMap) {
                    if (restoreCameraByPositionAndRadius != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(restoreCameraByPositionAndRadius));
                    } else if (UIUtils.isOnline(getActivity())) {
                        UIUtils.showSimpleToast(getActivity(), R.string.current_location_not_defined, Toast.LENGTH_LONG);
                    }
                }

                @Override
                public void useBaiduMap(BaiduMap baiduMap) {
                    Location location = lm.getLocation();
                    if (baiduMap != null && getActivity() != null && !getActivity().isFinishing() && location != null) {
                        com.baidu.mapapi.model.LatLng ll = new com.baidu.mapapi.model.LatLng(location.getLatitude(), location.getLongitude());
                        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(ll, zoomLevel + 1.4f));
                    }
                }
            });

        } else {
            MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
                @Override
                public void useGoogleMap(GoogleMap googleMap) {
                    if (restoreCameraByPins != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(restoreCameraByPins, display.getWidth(),
                                display.getHeight() - UIUtils.getPxFromDp(getActivity(), 150), 100));
                    }
                }

                @Override
                public void useBaiduMap(BaiduMap baiduMap) {
                    if (restoreCameraByBaiduPins != null) {
                        MapStatusUpdate cameraUpdate = MapStatusUpdateFactory.newLatLngBounds(restoreCameraByBaiduPins, display.getWidth(),
                                display.getHeight() - UIUtils.getPxFromDp(getActivity(), 150));
                        baiduMap.animateMapStatus(cameraUpdate);
                    }
                }
            });
        }
    }

    /**
     * Add marker with myu location on the map
     *
     * @param location - my current location
     */
    private void addMyLocation(final Location location) {
        if (location != null) {
            MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
                @Override
                public void useGoogleMap(GoogleMap googleMap) {

                    LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());

                    if (currentLocationMarker == null) {
                        currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                                .snippet(MY_LOCATION)
                                .position(coordinates)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)));
                    } else {
                        currentLocationMarker.setPosition(coordinates);
                    }
                }

                @Override
                public void useBaiduMap(BaiduMap baiduMap) {
                    if (baiduMap != null && getActivity() != null && !getActivity().isFinishing()) {
                        MyLocationData locData = new MyLocationData.Builder()
                                .latitude(location.getLatitude())
                                .longitude(location.getLongitude())
                                .build();
                        baiduMap.setMyLocationData(locData);
                    }
                }
            });
        }
    }

    MatrixLocationManager.CurrentLocationUpdateListener currentLocationUpdateListener = new MatrixLocationManager.CurrentLocationUpdateListener() {
        @Override
        public void onUpdate(Location location) {
            L.i(TAG, "Current location pin updated " + location.getLatitude() + ", " + location.getLongitude() + ", "
                    + "Provider: " + location.getProvider());
            //clearMap();

            if (preferencesManager.getUseLocationServices() && lm.isConnected()) {
                //loadTasksFromLocalDb();
                addMyLocation(location);
            }
        }
    };

    /**
     * Add Radius
     *
     * @param location - should be not null
     */

    private void addRadius(Location location) {
        if (location != null && getActivity() != null && !isTracking) {
            Resources r = getActivity().getResources();
            addCircle(location.getLatitude(), location.getLongitude(), taskRadius, r.getColor(R.color.map_radius_stroke),
                    r.getColor(android.R.color.transparent));
        }
    }

    /**
     * Draw circle on the map
     *
     * @param latitude  - current latitude
     * @param longitude - current longitude
     * @param radius    - current radius
     */
    private void addCircle(final double latitude, final double longitude, final int radius, final int strokeColor, final int fillColor) {
        MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
            @Override
            public void useGoogleMap(GoogleMap googleMap) {
                LatLng coordinates = new LatLng(latitude, longitude);

                if (circle == null) {
                    circle = googleMap.addCircle(new CircleOptions()
                            .center(coordinates)
                            .radius(radius)
                            .strokeColor(strokeColor)
                            .strokeWidth(6f));
                } else {
                    circle.setCenter(coordinates);
                    circle.setRadius(radius);
                }
            }

            @Override
            public void useBaiduMap(BaiduMap baiduMap) {
                com.baidu.mapapi.model.LatLng coordinates = new com.baidu.mapapi.model.LatLng(latitude, longitude);

                circleBaidu = baiduMap.addOverlay(new com.baidu.mapapi.map.CircleOptions()
                        .center(coordinates)
                        .fillColor(fillColor)
                        .stroke(new Stroke(3, strokeColor))
                        .radius(radius));
            }
        });
    }

    private void refreshIconState(boolean isLoading) {
        if (refreshButton != null && getActivity() != null) {
            if (isLoading) {
                refreshButton.setClickable(false);
                refreshButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
            } else {
                refreshButton.setClickable(true);
                refreshButton.clearAnimation();
            }
        }
    }

    private void setRadiusText() {
        String distance = String.format(Locale.US, "%.1f", (float) taskRadius / METERS_IN_KM);
        txtRadius.setText(distance + " " + getString(R.string.distance_km));
    }

    public void clearMap() {
        MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
            @Override
            public void useGoogleMap(GoogleMap googleMap) {
                googleMap.clear();
                currentLocationMarker = null;
                circle = null;
            }

            @Override
            public void useBaiduMap(BaiduMap baiduMap) {
                if (baiduMap != null && getActivity() != null && !getActivity().isFinishing()) {
                    try {
                        baiduMap.clear();
                        baiduMap.setMyLocationData(new MyLocationData.Builder().build());
                    } catch (Exception e) {
                        L.e(TAG, "Clean Baidu map error", e);
                    }
                }
            }
        });
    }

    public void clearPins() {
        if (clusterkraf != null) {
            clusterkraf.clear();
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
