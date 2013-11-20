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
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.util.HashMap;

public class TasksMapFragment extends Fragment {

    private static final String TAG = TasksMapFragment.class.getSimpleName();
    private View fragmentView;
    private GoogleMap map;
    private CameraPosition restoreCameraPosition;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() [fragmentView  =  " + fragmentView + ", savedInstanceState=" + savedInstanceState + "]");


        if (fragmentView != null) {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
                L.w(TAG, "parent.removeView(fragmentView);!!!!!!!!!!!1 ");
            }
        }
        try {
            fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
            L.w(TAG, "map is already there, just return view as it is ");
        }

        setHasOptionsMenu(true);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
        MatrixLocationManager lm = App.getInstance().getLocationManager();
        Location coords = lm.getLocation();
        if (coords != null) {
            loadNearbyStores(coords);
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.looking_for_location);
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    loadNearbyStores(location);
                }
            });
        }
    }

    private void showTaskDetails(Long taskId) {
        Intent intent = new Intent(getActivity(), TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        startActivity(intent);
    }

    public void onLoadingComplete(ArrayList<Task> list) {
        inputPoints.clear();
        for (Task item : list) {
            inputPoints.add(new InputPoint(item.getLatLng(), item));
        }
        Log.i(TAG, "[tasks.size=" + inputPoints.size() + "]");
        if (inputPoints.size() > 0) {
            initClusterkraf();
        } else if (getActivity() != null) {
            UIUtils.showSimpleToast(getActivity(), R.string.no_tasks_found, Toast.LENGTH_LONG);
        }
    }

    public void loadNearbyStores(Location location) {
        if (location != null) {
            loadNearbyStores(location.getLatitude(), location.getLongitude(), 5000, 100);
        }
    }

    public void loadNearbyStores(double latitude, double longitude, int radius, int limit) {
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
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                map = mapFragment.getMap();
                if (map != null) {
                    UiSettings uiSettings = map.getUiSettings();
                    uiSettings.setAllGesturesEnabled(false);
                    uiSettings.setScrollGesturesEnabled(true);
                    uiSettings.setZoomGesturesEnabled(true);
                    map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition arg0) {
                            moveMapCameraToBoundsAndInitClusterkraf();
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
            try {
                if (restoreCameraPosition != null) {
                    /**
                     * if a restoreCameraPosition is available, move the camera
                     * there
                     */
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(restoreCameraPosition));
                    restoreCameraPosition = null;
                } else {
                    //TODO: Move camera to my location or ???
                    L.w(TAG, "TODO: Move camera to my location or ???");
                }
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
     * @return
     */
    private int getPixelDistanceToJoinCluster() {
        return convertDeviceIndependentPixelsToPixels(this.options.dipDistanceToJoinCluster);
    }

    /**
     *
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
            L.d(TAG, "onMarkerClick() " + marker.getTitle() + ", ID=" + marker.getId() + "]");
            return false;
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
        int transitionDuration = 500;
        String transitionInterpolator = LinearInterpolator.class.getCanonicalName();
        int dipDistanceToJoinCluster = 100;
        int zoomToBoundsAnimationDuration = 500;
        int showInfoWindowAnimationDuration = 500;
        double expandBoundsFactor = 0.5d;
        com.twotoasters.clusterkraf.Options.SinglePointClickBehavior singlePointClickBehavior = com.twotoasters.clusterkraf.Options.SinglePointClickBehavior.SHOW_INFO_WINDOW;
        Options.ClusterClickBehavior clusterClickBehavior = Options.ClusterClickBehavior.ZOOM_TO_BOUNDS;
        Options.ClusterInfoWindowClickBehavior clusterInfoWindowClickBehavior = Options.ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS;
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
            render(marker, mContents, clusterPoint);
            return mContents;
        }

        @Override
        public View getInfoWindow(Marker marker, ClusterPoint clusterPoint) {
            render(marker, mWindow, clusterPoint);
            return mWindow;
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
}