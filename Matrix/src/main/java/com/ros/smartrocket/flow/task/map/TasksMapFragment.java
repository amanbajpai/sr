package com.ros.smartrocket.flow.task.map;

import android.content.res.Resources;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Stroke;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.BaseFragment;
import com.ros.smartrocket.flow.task.AllTaskFragment;
import com.ros.smartrocket.flow.task.TaskMvpView;
import com.ros.smartrocket.flow.wave.WaveMvpPresenter;
import com.ros.smartrocket.flow.wave.WaveMvpView;
import com.ros.smartrocket.flow.wave.WavePresenter;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.interfaces.SwitchCheckedChangeListener;
import com.ros.smartrocket.map.CurrentLocatiuonListener;
import com.ros.smartrocket.map.MapHelper;
import com.ros.smartrocket.map.OnMapStatusChangeFinishedListener;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.ui.fragment.TransparentSupportMapFragment;
import com.ros.smartrocket.ui.views.CustomSwitch;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TasksMapFragment extends BaseFragment implements TaskMvpView, WaveMvpView, SwitchCheckedChangeListener,
        OnMarkerClickDownstreamListener, OnInfoWindowClickDownstreamListener {
    private static final String TAG = TasksMapFragment.class.getSimpleName();
    private static final String MY_LOCATION = "MyLoc";
    // 1% = 200m => Max = 20km
    private static final int RADIUS_DELTA = 200;
    private static final float DEFAULT_ZOOM_LEVEL = 11f;
    private static final int METERS_IN_KM = 1000;
    public static final int DELAY_MILLIS = 1000;
    public static int DEFAULT_TASK_RADIUS = 5000;
    public static int taskRadius = DEFAULT_TASK_RADIUS;

    @BindView(R.id.btnFilter)
    ImageView btnFilter;
    @BindView(R.id.roundImage)
    ImageView roundImage;
    @BindView(R.id.txtRadius)
    CustomTextView txtRadius;
    @BindView(R.id.seekBarRadius)
    SeekBar seekBarRadius;
    @BindView(R.id.showHiddenTasksToggleButton)
    CustomSwitch showHiddenTasksToggleButton;
    @BindView(R.id.hideMissionsLayout)
    View hideMissionsLayout;
    @BindView(R.id.hidden_panel)
    LinearLayout rlFilterPanel;
    Unbinder unbinder;
    private ImageView refreshButton;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private GoogleMap googleMap;
    private BaiduMap baiduMap;
    private CameraPosition cameraPositionByRadiusAndLocation;
    private LatLngBounds cameraBoundsByPins;
    private com.baidu.mapapi.model.LatLngBounds baiduCameraBoundsByPins;
    private Display display;
    private Clusterkraf clusterkraf;
    private com.twotoasters.baiduclusterkraf.Clusterkraf baiduClusterkraf;
    private Keys.MapViewMode mode;
    private Marker currentLocationMarker;
    private Circle circle;
    private MapTaskMvpPresenter<TaskMvpView> taskPresenter;
    private WaveMvpPresenter<WaveMvpView> wavePresenter;
    private int sbRadiusProgress = 5;
    private boolean isTouchTracking = false;
    private float zoomLevel = DEFAULT_ZOOM_LEVEL;
    private boolean isFilterShow = false;
    private float mapWidth;
    private int viewItemId = 0;
    private boolean isFirstStart = true;
    private boolean isNeedRefresh = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_map, null);
        LinearLayout mapLayout = (LinearLayout) view.findViewById(R.id.mapLayout);
        if (savedInstanceState == null && mapLayout.findViewById(R.id.map) == null) {
            setUpMap(mapLayout);
        }
        unbinder = ButterKnife.bind(this, view);
        initUI();
        lm.setCurrentLocationUpdateListener(currentLocationUpdateListener);
        taskPresenter = new MapTaskPresenter<>();
        wavePresenter = new WavePresenter<>();
        return view;
    }

    private void setUpMap(LinearLayout mapLayout) {
        try {
            if (Config.USE_BAIDU)
                mapLayout.addView(LayoutInflater.from(getActivity()).inflate(R.layout.fragment_baidu_map, null));
            else
                mapLayout.addView(LayoutInflater.from(getActivity()).inflate(R.layout.fragment_google_map, null));
        } catch (Exception e) {
            L.e(TAG, "Error in onCreateView method.", e);
            getActivity().finish();
        }
    }

    private void initUI() {
        initRefreshButton();
        refreshIconState(true);
        display = getActivity().getWindowManager().getDefaultDisplay();
        mapWidth = UIUtils.getDpFromPx(getActivity(),
                display.getWidth() - UIUtils.getPxFromDp(getActivity(), 20));
        roundImage.setImageResource(Config.USE_BAIDU ? R.drawable.round_baidu : R.drawable.round);
        showHiddenTasksToggleButton.setChecked(preferencesManager.getShowHiddenTask());
        showHiddenTasksToggleButton.setOnCheckedChangeListener(this);
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouchTracking = true;
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
                isTouchTracking = false;
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
        taskPresenter.attachView(this);
        wavePresenter.attachView(this);
        initTaskRadius();
        if (!isHidden() && isNeedRefresh) {
            setViewMode(getArguments());
            isNeedRefresh = false;
            initMap();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        storeZoomAndRadius();
        taskPresenter.detachView();
        wavePresenter.detachView();
    }

    private void storeZoomAndRadius() {
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
            taskPresenter.attachView(this);
            wavePresenter.attachView(this);
            setViewMode(getArguments());
            loadData(false);
            showHiddenTasksToggleButton.setChecked(preferencesManager.getShowHiddenTask());
        } else if (isFilterShow) {
            showFilterPanel(false);
        }
    }

    private void initMap() {
        if (!MapHelper.isMapNotNull(googleMap, baiduMap)) {
            if (Config.USE_BAIDU) {
                baiduMap = MapHelper.getBaiduMap(getActivity(), new OnMapStatusChangeFinishedListener() {
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

    private void setViewMode(Bundle bundle) {
        if (bundle != null) {
            mode = Keys.MapViewMode.valueOf(bundle.getString(Keys.MAP_MODE_VIEWTYPE));
            boolean showFilterButton = mode == Keys.MapViewMode.ALL_TASKS || mode == Keys.MapViewMode.WAVE_TASKS;
            btnFilter.setVisibility(showFilterButton ? View.VISIBLE : View.INVISIBLE);
            if (mode == Keys.MapViewMode.WAVE_TASKS) hideMissionsLayout.setVisibility(View.GONE);

            if (mode == Keys.MapViewMode.WAVE_TASKS || mode == Keys.MapViewMode.SINGLE_TASK)
                viewItemId = bundle.getInt(Keys.MAP_VIEW_ITEM_ID);
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

    private void loadData(boolean serverUpdate) {
        if (preferencesManager.getUseLocationServices() && lm.isConnected()) {
            AllTaskFragment.stopRefreshProgress = !serverUpdate;
            refreshIconState(true);
            loadTasksFromLocalDb();
            if (serverUpdate) updateDataFromServer();
        } else {
            refreshIconState(false);
        }
    }

    private void loadTasksFromLocalDb() {
        if (mode == Keys.MapViewMode.WAVE_TASKS || mode == Keys.MapViewMode.SINGLE_TASK) {
            if (getActivity() != null) {
                showLoading(true);
            }
        }

        new Handler().postDelayed(() -> {
            if (getActivity() != null) {
                hideLoading();
                if (preferencesManager.getUseLocationServices())
                    taskPresenter.loadTasksFromDb(viewItemId, showHiddenTasksToggleButton.isChecked(), mode);
            }
        }, DELAY_MILLIS);
    }

    private void updateDataFromServer() {
        if (UIUtils.isOnline(getActivity())) {
            if (mode == Keys.MapViewMode.MY_TASKS)
                taskPresenter.getMyTasksFromServer();
            else if (mode == Keys.MapViewMode.ALL_TASKS)
                getWavesFromServer(taskRadius);
        } else {
            refreshIconState(false);
            UIUtils.showSimpleToast(getActivity(), R.string.no_internet);
        }
    }

    private void getWavesFromServer(final int radius) {
        MatrixLocationManager.getCurrentLocation(false, new CurrentLocatiuonListener() {
            @Override
            public void getLocationSuccess(Location location) {
                if (isFirstStart)
                    new Handler().postDelayed(() ->
                            wavePresenter.getWavesFromServer(location.getLatitude(), location.getLongitude(), radius), DELAY_MILLIS);
                else
                    wavePresenter.getWavesFromServer(location.getLatitude(), location.getLongitude(), radius);
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

    @Override
    public void onTaskLoadingComplete(List<Task> list) {
        if (AllTaskFragment.stopRefreshProgress) refreshIconState(false);

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
                        if (lm.getLastGooglePosition() != null)
                            googleMap.moveCamera(
                                    CameraUpdateFactory.newCameraPosition(new CameraPosition(lm.getLastGooglePosition(), zoomLevel, 0, 0)));
                    }

                    @Override
                    public void useBaiduMap(BaiduMap baiduMap) {
                        if (baiduMap != null && getActivity() != null && !getActivity().isFinishing() && lm.getLastBaiduPositionPosition() != null)
                            baiduMap.setMapStatus(
                                    MapStatusUpdateFactory.newLatLngZoom(lm.getLastBaiduPositionPosition(), lm.getZoomLevel() + 1.4f));
                    }
                });
            } else if (isFirstStart) moveCameraToLocation();
        }
        isFirstStart = false;
        if (preferencesManager.getUseLocationServices()) addMyLocation(location);

    }

    @Override
    public void onTasksLoaded() {
        AllTaskFragment.stopRefreshProgress = true;
        loadTasksFromLocalDb();
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

            if (task == null) return false;

            View overlayView = LayoutInflater.from(getActivity()).inflate(R.layout.map_info_window, null);
            MapHelper.setMapOverlayView(getActivity(), overlayView, task);

            InfoWindow.OnInfoWindowClickListener listener = () -> {
                MapHelper.mapOverlayClickResult(getActivity(), task.getId(), task.getMissionId(), task.getStatusId());
                baiduMap.hideInfoWindow();
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
    com.twotoasters.baiduclusterkraf.OnMarkerClickDownstreamListener onMarkerClickDownstreamListener = (marker, clusterPoint) -> false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        initRefreshButton();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initRefreshButton() {
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (refreshButton == null && actionBar != null) {
            View view = actionBar.getCustomView();
            if (view != null) {
                refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
                if (refreshButton != null) refreshButton.setOnClickListener(v -> onRefreshClick());
            }
        }
    }

    private void onRefreshClick() {
        loadData(true);
        IntentUtils.refreshProfileAndMainMenu(getActivity());
        IntentUtils.refreshMainMenuMyTaskCount(getActivity());
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
        if (marker != null)
            try {
                String[] taskData = marker.getSnippet().split("_");
                int taskId = Integer.valueOf(taskData[0]);
                int taskStatusId = Integer.valueOf(taskData[2]);
                int missionId = Integer.valueOf(taskData[3]);

                MapHelper.mapOverlayClickResult(getActivity(), taskId, missionId, taskStatusId);
            } catch (Exception e) {
                L.e(TAG, "Error info vindow click" + e, e);
            }
        return false;
    }

    private void restoreCameraPositionByPins(Location location, ArrayList<InputPoint> inputPoints) {
        if (location != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (InputPoint point : inputPoints)
                builder.include(point.getMapPosition());

            builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
            cameraBoundsByPins = /*!inputPoints.isEmpty() ?*/ builder.build() /*: null*/;
        }
    }

    private void restoreCameraPositionByBaiduPins(Location location, ArrayList<com.twotoasters.baiduclusterkraf.InputPoint> inputPoints) {
        if (location != null) {
            com.baidu.mapapi.model.LatLngBounds.Builder builder = new com.baidu.mapapi.model.LatLngBounds.Builder();
            for (com.twotoasters.baiduclusterkraf.InputPoint point : inputPoints) {
                builder.include(point.getMapPosition());
            }
            builder.include(new com.baidu.mapapi.model.LatLng(location.getLatitude(), location.getLongitude()));
            baiduCameraBoundsByPins = /*!inputPoints.isEmpty() ?*/ builder.build() /*: null*/;
        }
    }

    private void restoreCameraPositionByRadius(Location location, int radius) {
        if (location != null) {
            zoomLevel = MapHelper.getZoomForMetersWide(mapWidth, radius * 2, location.getLatitude());
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            cameraPositionByRadiusAndLocation = new CameraPosition(coordinates, zoomLevel, 0, 0);
        }
    }

    private void moveCameraToLocation() {
        if (mode == Keys.MapViewMode.ALL_TASKS || mode == Keys.MapViewMode.WAVE_TASKS) {
            MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
                @Override
                public void useGoogleMap(GoogleMap googleMap) {
                    if (cameraPositionByRadiusAndLocation != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPositionByRadiusAndLocation));
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
                    if (cameraBoundsByPins != null)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(cameraBoundsByPins, display.getWidth(),
                                display.getHeight() - UIUtils.getPxFromDp(getActivity(), 150), 100));

                }

                @Override
                public void useBaiduMap(BaiduMap baiduMap) {
                    if (baiduCameraBoundsByPins != null) {
                        MapStatusUpdate cameraUpdate = MapStatusUpdateFactory.newLatLngBounds(baiduCameraBoundsByPins, display.getWidth(),
                                display.getHeight() - UIUtils.getPxFromDp(getActivity(), 150));
                        baiduMap.animateMapStatus(cameraUpdate);
                    }
                }
            });
        }
    }

    private void addMyLocation(final Location location) {
        if (location != null) {
            MapHelper.mapChooser(googleMap, baiduMap, new MapHelper.SelectMapInterface() {
                @Override
                public void useGoogleMap(GoogleMap googleMap) {

                    LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());

                    if (currentLocationMarker == null)
                        currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                                .snippet(MY_LOCATION)
                                .position(coordinates)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon)));
                    else
                        currentLocationMarker.setPosition(coordinates);
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

    MatrixLocationManager.CurrentLocationUpdateListener currentLocationUpdateListener = location -> {
        if (preferencesManager.getUseLocationServices() && lm.isConnected())
            addMyLocation(location);
    };

    private void addRadius(Location location) {
        if (location != null && getActivity() != null && !isTouchTracking) {
            Resources r = getActivity().getResources();
            addCircle(location.getLatitude(), location.getLongitude(), taskRadius, r.getColor(R.color.map_radius_stroke),
                    r.getColor(android.R.color.transparent));
        }
    }

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
                baiduMap.addOverlay(new com.baidu.mapapi.map.CircleOptions()
                        .center(coordinates)
                        .fillColor(fillColor)
                        .stroke(new Stroke(3, strokeColor))
                        .radius(radius));
            }
        });
    }

    @Override
    public void refreshIconState(boolean isLoading) {
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

    @Override
    public void onWavesLoaded() {
        AllTaskFragment.stopRefreshProgress = true;
        loadTasksFromLocalDb();
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
                if (baiduMap != null && getActivity() != null && !getActivity().isFinishing())
                    try {
                        baiduMap.clear();
                        baiduMap.setMyLocationData(new MyLocationData.Builder().build());
                    } catch (Exception e) {
                        L.e(TAG, "Clean Baidu map error", e);
                    }
            }
        });
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(getActivity(), networkError.getErrorMessageRes());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btnMyLocation, R.id.btnFilter, R.id.applyButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnMyLocation:
                moveCameraToLocation();
                break;
            case R.id.btnFilter:
                toggleFilterPanel();
                break;
            case R.id.applyButton:
                toggleFilterPanel();
                loadData(true);
                break;
        }
    }
}
