package com.matrix.fragment;

import android.content.Context;
import android.content.res.Resources;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matrix.App;
import com.matrix.db.entity.Task;
import com.matrix.utils.L;
import com.matrix.utils.UIUtils;
import com.twotoasters.clusterkraf.*;

import com.matrix.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class TasksMapFragment extends Fragment {

    private static final String TAG = TasksMapFragment.class.getSimpleName();
    private View fragmentView;
    private GoogleMap map;

    private boolean mLoading = false;

    private ArrayList<InputPoint> inputPoints;
    private Clusterkraf clusterkraf;
    private ClusterOptions options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputPoints = new ArrayList<InputPoint>();
        if (this.options == null) {
            this.options = new ClusterOptions();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() [fragmentView  =  " + fragmentView + ", savedInstanceState=" +savedInstanceState + "]");

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


        map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        setHasOptionsMenu(true);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Location coords = App.getInstance().getLocationManager().getLocation();
        if (coords != null) {
           this.loadNearbyStores(coords);
        }
    }

    private void loadStoreDetails(Long id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("storeId", String.valueOf(id));
    }

    public void onLoadingComplete() {

        Log.i(TAG, "[stores.size=" + inputPoints.size() + "]");
        if (inputPoints.size() > 0) {
            initClusterkraf();
        } else if (getActivity() != null) {
            UIUtils.showSimpleToast(getActivity(), R.string.no_tasks_found, Toast.LENGTH_LONG);
        }
    }

    public void loadNearbyStores(Location location) {
        if(location != null) {
            loadNearbyStores(location.getLatitude(), location.getLongitude(), 5000, 100);
        }
    }

    public void loadNearbyStores(double latitude, double longitude, int radius, int limit) {
        if (mLoading) {
            return;
        }

        //TODO get TASK List!
    }


    /* ==============================================
    * Methods for Clusters pins display on the map
    * ============================================== */

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

        options.setMarkerOptionsChooser(new StoreMarkerOptionsChooser(getActivity()));
        options.setOnMarkerClickDownstreamListener(onMarkerClickListener);
        options.setOnInfoWindowClickDownstreamListener(onInfoWindowClickListener);
        options.setInfoWindowDownstreamAdapter(new CustomInfoWindowAdapter());
        //options.setProcessingListener(this);
    }

    private int getPixelDistanceToJoinCluster() {
        return convertDeviceIndependentPixelsToPixels(this.options.dipDistanceToJoinCluster);
    }

    private int convertDeviceIndependentPixelsToPixels(int dip) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return Math.round(displayMetrics.density * dip);
    }

    private OnMarkerClickDownstreamListener onMarkerClickListener = new OnMarkerClickDownstreamListener() {
        @Override
        public boolean onMarkerClick(Marker marker, ClusterPoint clusterPoint) {
            //Log.d(TAG, "onMarkerClick() " + marker.getTitle() + ", ID=" + marker.getId() + "]");
            return false;
        }
    };

    private OnInfoWindowClickDownstreamListener onInfoWindowClickListener = new OnInfoWindowClickDownstreamListener() {
        @Override
        public boolean onInfoWindowClick(Marker marker, ClusterPoint clusterPoint) {
            //Log.d(TAG, "onInfoWindowClick() " + marker.getTitle() + ", ID=" + marker.getId() + "]");

            Long storeId = Long.valueOf(marker.getSnippet());
            loadStoreDetails(storeId);
            return false;
        }
    };

    /**
     * Chooser for
     */
    private class StoreMarkerOptionsChooser extends MarkerOptionsChooser {
        private final WeakReference<Context> contextRef;
        private final Paint clusterPaintLarge;
        private final Paint clusterPaintMedium;
        private final Paint clusterPaintSmall;

        public StoreMarkerOptionsChooser(Context context) {
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
            Context context = contextRef.get();
            if (context != null) {
                Resources res = context.getResources();
                boolean isCluster = clusterPoint.size() > 1;
                BitmapDescriptor icon;
                String title;
                if (isCluster) {
                    int clusterSize = clusterPoint.size();
                    icon = BitmapDescriptorFactory.fromBitmap(getClusterBitmap(res, R.drawable.ic_map_cluster_pin, clusterSize));
                    //title = res.getQuantityString(R.plurals.count_points, clusterSize, clusterSize);
                    title = "" + clusterSize;
                } else {
                    Task data = (Task)clusterPoint.getPointAtOffset(0).getTag();
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
    static class ClusterOptions {

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

        private void render(Marker marker, View view) {
            Long storeId = Long.valueOf(marker.getSnippet());
            Task model = getTaskById(storeId);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                titleUi.setText(title);
            }

            String rate = "";
            TextView rateText = ((TextView) view.findViewById(R.id.price_value));
            if (title != null) {
                rateText.setText(rate);
            }

            String distance = "";
            TextView distanceText = ((TextView) view.findViewById(R.id.distance_value));
            if (title != null) {
                distanceText.setText(distance);
            }
        }

        @Override
        public View getInfoContents(Marker marker, ClusterPoint clusterPoint) {
            render(marker, mContents);
            return mContents;
        }

        @Override
        public View getInfoWindow(Marker marker, ClusterPoint clusterPoint) {
            render(marker, mWindow);
            return mWindow;
        }
    }

    private Task getTaskById(Long taskId) {
        return new Task();
    }
}