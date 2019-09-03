package com.twotoasters.baiduclusterkraf;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Projection;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clusters InputPoint objects on a GoogleMap
 */
public class Clusterkraf {

    private static final String TAG = "Clusterkraf";

    private final WeakReference<BaiduMap> mapRef;
    private final Options options;
    private final ClusterTransitionsAnimation transitionsAnimation;

    private final InnerCallbackListener innerCallbackListener;

    private final ArrayList<InputPoint> points = new ArrayList<InputPoint>();
    private ArrayList<ClusterPoint> currentClusters;
    private ArrayList<Marker> currentMarkers;
    private HashMap<Marker, ClusterPoint> currentClusterPointsByMarker = new HashMap<Marker, ClusterPoint>();
    private ArrayList<ClusterPoint> previousClusters;
    private ArrayList<Marker> previousMarkers;
    private BaseClusteringTaskHost clusteringTaskHost;
    private ClusterTransitionsBuildingTaskHost clusterTransitionsBuildingTaskHost;

    /**
     * Add a single InputPoint for clustering
     *
     * @param inputPoint The InputPoint object to be clustered
     */
    public void add(InputPoint inputPoint) {
        // @TODO: test individually adding points
        if (inputPoint != null) {
            points.add(inputPoint);
            updateClustersAndTransition();
        }
    }

    /**
     * Construct a Clusterkraf instance to manage your map with customized
     * options and a list of points
     *
     * @param map     The GoogleMap to be managed by Clusterkraf
     * @param options Customized options
     */
    public Clusterkraf(BaiduMap map, Options options, ArrayList<InputPoint> points) {
        this.mapRef = new WeakReference<BaiduMap>(map);
        this.options = options;
        this.innerCallbackListener = new InnerCallbackListener(this);
        this.transitionsAnimation = new ClusterTransitionsAnimation(map, options, innerCallbackListener);

        if (points != null) {
            this.points.addAll(points);
        }

        if (map != null) {
            map.setOnMapStatusChangeListener(innerCallbackListener.clusteringOnCameraChangeListener);
            map.setOnMarkerClickListener(innerCallbackListener);
        }

        showAllClusters();
    }

    public BaiduMap getMap() {
        return mapRef.get();
    }

    /**
     * Add a list of InputPoint objects for clustering
     *
     * @param inputPoints The list of InputPoint objects for clustering
     */
    public void addAll(ArrayList<InputPoint> inputPoints) {
        if (inputPoints != null) {
            Log.d(TAG, "addAll()");
            points.addAll(inputPoints);
            updateClustersAndTransition();
        }
    }

    /**
     * Remove all existing InputPoint objects and add a new list of InputPoint
     * objects for clustering
     *
     * @param inputPoints The new list of InputPoint objects for clustering
     */
    public void replace(ArrayList<InputPoint> inputPoints) {
        clear();
        addAll(inputPoints);
    }

    /**
     * Remove all Clusterkraf-managed markers from the map
     */
    public void clear() {
        /**
         * cancel the background thread clustering task
         */
        if (clusteringTaskHost != null) {
            clusteringTaskHost.cancel();
            clusteringTaskHost = null;
        }
        /**
         * cancel the background thread transition building task
         */
        if (clusterTransitionsBuildingTaskHost != null) {
            clusterTransitionsBuildingTaskHost.cancel();
            clusterTransitionsBuildingTaskHost = null;
        }

        /**
         * we avoid GoogleMap.clear() so users can manage their own
         * non-clustered markers on the map.
         *
         * @see http://code.google.com/p/gmaps-api-issues/issues/detail?id=4703
         */
        if (currentMarkers != null) {
            for (Marker marker : currentMarkers) {
                if (marker != null) {
                    marker.remove();
                }
            }
        }

        currentClusters = null;
        currentClusterPointsByMarker = null;
        currentMarkers = null;
        previousClusters = null;
        previousMarkers = null;

        points.clear();
        Log.d(TAG, "clear()");
    }

    // TODO: support removing individual InputPoint objects
    private void drawMarkers() {
        BaiduMap map = mapRef.get();
        if (map != null && currentClusters != null) {
            Log.d(TAG, "drawMarkers()[currentClusters.size=" + currentClusters.size() + "]");
            currentMarkers = new ArrayList<Marker>(currentClusters.size());
            currentClusterPointsByMarker = new HashMap<Marker, ClusterPoint>(currentClusters.size());
            MarkerOptionsChooser moc = options.getMarkerOptionsChooser();
            for (ClusterPoint clusterPoint : currentClusters) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(clusterPoint.getMapPosition());
                if (moc != null) {
                    moc.choose(markerOptions, clusterPoint);
                }

                Marker marker = (Marker) (map.addOverlay(markerOptions));
                currentMarkers.add(marker);
                currentClusterPointsByMarker.put(marker, clusterPoint);
            }
            Log.d(TAG, "drawMarkers()[size=" + currentClusterPointsByMarker.size() + "]");
        }
    }

    private void removePreviousMarkers() {
        BaiduMap map = mapRef.get();
        if (map != null && previousClusters != null && previousMarkers != null) {
            for (Marker marker : previousMarkers) {
                marker.remove();
            }
            previousMarkers = null;
            previousClusters = null;
        }
    }

    private void updateClustersAndTransition() {
        previousClusters = currentClusters;
        previousMarkers = currentMarkers;
        startClusteringTask();
    }

    private void startClusteringTask() {
        clusteringTaskHost = new UpdateClustersAndTransitionClusteringTaskHost();
        clusteringTaskHost.executeTask();
    }

    private void transitionClusters(ClusterTransitions clusterTransitions) {
        if (clusterTransitions != null) {
            transitionsAnimation.animate(clusterTransitions);
        }
        clusterTransitionsBuildingTaskHost = null;
    }

    private void startClusterTransitionsBuildingTask(Projection projection) {
        clusterTransitionsBuildingTaskHost = new ClusterTransitionsBuildingTaskHost();
        clusterTransitionsBuildingTaskHost.executeTask(projection);

        clusteringTaskHost = null;
    }

    private void showAllClusters() {
        if (clusteringTaskHost != null) {
            drawMarkers();
            clusteringTaskHost = null;
        } else {
            clusteringTaskHost = new ShowAllClustersClusteringTaskHost();
            clusteringTaskHost.executeTask();
        }
    }

    /**
     * Animate the camera so all of the InputPoint objects represented by the
     * passed ClusterPoint are in view
     *
     * @param clusterPoint
     */
    public void zoomToBounds(ClusterPoint clusterPoint) {
        BaiduMap map = mapRef.get();
        if (map != null && clusterPoint != null) {
            innerCallbackListener.clusteringOnCameraChangeListener.setDirty(System.currentTimeMillis());

            //TODO use newLatLngBounds
            MapStatusUpdate cameraUpdate = MapStatusUpdateFactory.newLatLngBounds(clusterPoint.getBoundsOfInputPoints());
            map.animateMapStatus(cameraUpdate, options.getShowInfoWindowAnimationDuration());
        }
    }

    /**
     * Show the InfoWindow for the passed Marker and ClusterPoint
     *
     * @param marker
     * @param clusterPoint
     */
    public void showInfoWindow(Marker marker, ClusterPoint clusterPoint) {
        BaiduMap map = mapRef.get();
        if (map != null && marker != null && clusterPoint != null) {
            long dirtyUntil = System.currentTimeMillis() + options.getShowInfoWindowAnimationDuration();
            innerCallbackListener.clusteringOnCameraChangeListener.setDirty(dirtyUntil);

            MapStatusUpdate cameraUpdate = MapStatusUpdateFactory.newLatLng(marker.getPosition());
            map.animateMapStatus(cameraUpdate, options.getShowInfoWindowAnimationDuration()/*, new CancelableCallback() {

                @Override
                public void onFinish() {
                    innerCallbackListener.handler.post(new Runnable() {

                        @Override
                        public void run() {
                            innerCallbackListener.clusteringOnCameraChangeListener.setDirty(0);

                        }
                    });
                }

                @Override
                public void onCancel() {
                    innerCallbackListener.clusteringOnCameraChangeListener.setDirty(0);
                }
            }*/);

            options.getOnShowInfoWindowListener().onShowInfoWindow(marker, clusterPoint);

        }
    }

    private static class InnerCallbackListener implements ClusteringOnCameraChangeListener.Host, ClusterTransitionsAnimation.Host, OnMarkerClickListener {

        private final WeakReference<Clusterkraf> clusterkrafRef;

        private final Handler handler = new Handler();

        private InnerCallbackListener(Clusterkraf clusterkraf) {
            clusterkrafRef = new WeakReference<Clusterkraf>(clusterkraf);
            clusteringOnCameraChangeListener = new ClusteringOnCameraChangeListener(this, clusterkraf.options);
        }

        private final ClusteringOnCameraChangeListener clusteringOnCameraChangeListener;

        @Override
        public void onClusteringCameraChange() {
            Clusterkraf clusterkraf = clusterkrafRef.get();
            if (clusterkraf != null) {
                clusterkraf.getMap().hideInfoWindow();
                if (clusterkraf.clusteringTaskHost != null) {
                    clusterkraf.clusteringTaskHost.cancel();
                    clusterkraf.clusteringTaskHost = null;
                }
                if (clusterkraf.clusterTransitionsBuildingTaskHost != null) {
                    clusterkraf.clusterTransitionsBuildingTaskHost.cancel();
                    clusterkraf.clusterTransitionsBuildingTaskHost = null;
                }
                clusterkraf.transitionsAnimation.cancel();
                clusterkraf.updateClustersAndTransition();
            }
        }

        /**
         * @see com.twotoasters.baiduclusterkraf.ClusterTransitionsAnimation.Host#
         * onClusterTransitionStarting()
         */
        @Override
        public void onClusterTransitionStarting() {
            clusteringOnCameraChangeListener.setDirty(System.currentTimeMillis());
        }

        /**
         * @see com.twotoasters.baiduclusterkraf.ClusterTransitionsAnimation.Host#
         * onClusterTransitionStarted()
         */
        @Override
        public void onClusterTransitionStarted() {
            Clusterkraf clusterkraf = clusterkrafRef.get();
            if (clusterkraf != null) {
                /**
                 * now that the first frame of the transition has been drawn, we
                 * can remove our previous markers without suffering any
                 * blinking markers
                 */
                clusterkraf.removePreviousMarkers();
            }
        }

        /**
         * @see com.twotoasters.baiduclusterkraf.ClusterTransitionsAnimation.Host#
         * onClusterTransitionFinished()
         */
        @Override
        public void onClusterTransitionFinished() {
            Clusterkraf clusterkraf = clusterkrafRef.get();
            if (clusterkraf != null) {
                try {
                    clusterkraf.drawMarkers();
                } catch (Exception e) {
                    Log.e("InnerCallbackListener", "Error in onClusterTransitionFinished method.", e);
                }
                /**
                 * now that we have drawn our new set of markers, we can let the
                 * transitionsAnimation know so it can clear its markers
                 */
                clusterkraf.transitionsAnimation.onHostPlottedDestinationClusterPoints();
            }
            clusteringOnCameraChangeListener.setDirty(0);
        }


        @Override
        public boolean onMarkerClick(Marker marker) {
            boolean handled = false;
            boolean exempt = false;
            Clusterkraf clusterkraf = clusterkrafRef.get();
            if (clusterkraf != null && clusterkraf.currentClusterPointsByMarker != null) {
                ClusterPoint clusterPoint = clusterkraf.currentClusterPointsByMarker.get(marker);
                Log.d(TAG, "1. [clusterPoint=" + clusterPoint + "], size="
                        + clusterkraf.currentClusterPointsByMarker.size());
                if (clusterPoint == null) {
                    if (clusterkraf.transitionsAnimation.getAnimatedDestinationClusterPoint(marker) != null) {
                        exempt = true;
                        // animated marker click is not supported
                    } else {
                        clusterPoint = clusterkraf.transitionsAnimation.getStationaryClusterPoint(marker);
                    }
                }
                Log.d(TAG, "2. [clusterPoint=" + clusterPoint + ", exempt=" + exempt + "]");
                OnMarkerClickDownstreamListener downstreamListener = clusterkraf.options.getOnMarkerClickDownstreamListener();
                if (exempt == false && downstreamListener != null) {
                    handled = downstreamListener.onMarkerClick(marker, clusterPoint);
                }
                if (exempt == false && handled == false && clusterPoint != null) {
                    if (clusterPoint.size() > 1) {
                        switch (clusterkraf.options.getClusterClickBehavior()) {
                            case ZOOM_TO_BOUNDS:
                                clusterkraf.zoomToBounds(clusterPoint);
                                handled = true;
                                break;
                            case SHOW_INFO_WINDOW:
                                clusterkraf.showInfoWindow(marker, clusterPoint);
                                handled = true;
                                break;
                            case NO_OP:
                                // no-op
                                break;
                        }
                    } else {
                        switch (clusterkraf.options.getSinglePointClickBehavior()) {
                            case SHOW_INFO_WINDOW:
                                clusterkraf.showInfoWindow(marker, clusterPoint);
                                handled = true;
                                break;
                            case NO_OP:
                                // no-op
                                break;
                        }
                    }
                }
            }
            return handled || exempt;
        }

        /**
         * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#
         * onInfoWindowClick(com.google.android.gms.maps.model.Marker)
         */
        /*@Override
        public void onInfoWindowClick() {
            Clusterkraf clusterkraf = clusterkrafRef.get();
            if (clusterkraf != null) {
                boolean handled = false;
                ClusterPoint clusterPoint = clusterkraf.currentClusterPointsByMarker.get(marker);
                OnInfoWindowClickListener downstreamListener = clusterkraf.options.getOnInfoWindowClickListener();
                if (downstreamListener != null) {
                    handled = downstreamListener.onInfoWindowClick(marker, clusterPoint);
                }
                if (handled == false && clusterPoint != null) {
                    if (clusterPoint.size() > 1) {
                        switch (clusterkraf.options.getClusterInfoWindowClickBehavior()) {
                            case ZOOM_TO_BOUNDS:
                                clusterkraf.zoomToBounds(clusterPoint);
                                break;
                            case HIDE_INFO_WINDOW:
                                BaiduMap map = clusterkraf.getMap();
                                if (map != null) {
                                    map.hideInfoWindow();
                                }
                                break;
                            case NO_OP:
                                // no-op
                                break;
                        }
                    } else {
                        switch (clusterkraf.options.getSinglePointInfoWindowClickBehavior()) {
                            case HIDE_INFO_WINDOW:
                                BaiduMap map = clusterkraf.getMap();
                                if (map != null) {
                                    map.hideInfoWindow();
                                }
                                break;
                            case NO_OP:
                                // no-op
                                break;
                        }
                    }
                }

            }

        }*/

        /**
         * @see com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
         * getInfoWindow(com.google.android.gms.maps.model.Marker)
         */
        /*@Override
        public View getInfoWindow(Marker marker) {
            View infoWindow = null;
            Clusterkraf clusterkraf = clusterkrafRef.get();
            if (clusterkraf != null && clusterkraf.currentClusterPointsByMarker != null) {
                ClusterPoint clusterPoint = clusterkraf.currentClusterPointsByMarker.get(marker);
                InfoWindowDownstreamAdapter infoWindowDownstreamAdapter = clusterkraf.options.getInfoWindowDownstreamAdapter();
                if (infoWindowDownstreamAdapter != null) {
                    infoWindow = infoWindowDownstreamAdapter.getInfoWindow(marker, clusterPoint);
                }
            }
            return infoWindow; // Google Map will handle it when null
        }*/

        /**
         * @see com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
         * getInfoContents(com.google.android.gms.maps.model.Marker)
         */
        /*@Override
        public View getInfoContents(Marker marker) {
            View infoWindow = null;
            Clusterkraf clusterkraf = clusterkrafRef.get();
            if (clusterkraf != null && clusterkraf.currentClusterPointsByMarker != null) {
                ClusterPoint clusterPoint = clusterkraf.currentClusterPointsByMarker.get(marker);
                InfoWindowDownstreamAdapter infoWindowDownstreamAdapter = clusterkraf.options.getInfoWindowDownstreamAdapter();
                if (infoWindowDownstreamAdapter != null) {
                    infoWindow = infoWindowDownstreamAdapter.getInfoContents(marker, clusterPoint);
                }
            }
            return infoWindow; // Google Map will handle it when null
        }*/
    }

    abstract private class BaseClusteringTaskHost implements ClusteringTask.Host {

        private ClusteringTask task;

        BaseClusteringTaskHost() {
            this.task = new ClusteringTask(this);
        }

        @Override
        public void onClusteringTaskPostExecute(ClusteringTask.Result result) {
            currentClusters = result.currentClusters;
            onCurrentClustersSet(result);
            task = null;
        }

        public void cancel() {
            ProcessingListener processingListener = options.getProcessingListener();
            if (processingListener != null) {
                processingListener.onClusteringFinished();
            }
            task.cancel(true);
            task = null;
        }

        @SuppressLint("NewApi")
        public void executeTask() {
            BaiduMap map = mapRef.get();
            if (map != null) {
                ProcessingListener processingListener = options.getProcessingListener();
                if (processingListener != null) {
                    processingListener.onClusteringStarted();
                }

                ClusteringTask.Argument arg = new ClusteringTask.Argument();
                arg.projection = map.getProjection();
                arg.options = options;
                arg.points = points;
                arg.previousClusters = previousClusters;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arg);
                } else {
                    task.execute(arg);
                }
            }

        }

        abstract protected void onCurrentClustersSet(ClusteringTask.Result result);

    }

    private class ShowAllClustersClusteringTaskHost extends BaseClusteringTaskHost {

        @Override
        protected void onCurrentClustersSet(ClusteringTask.Result result) {
            ProcessingListener processingListener = options.getProcessingListener();
            if (processingListener != null) {
                processingListener.onClusteringFinished();
            }
            showAllClusters();
        }
    }

    private class UpdateClustersAndTransitionClusteringTaskHost extends BaseClusteringTaskHost {
        @Override
        protected void onCurrentClustersSet(ClusteringTask.Result result) {
            startClusterTransitionsBuildingTask(result.projection);
        }
    }

    private class ClusterTransitionsBuildingTaskHost implements ClusterTransitionsBuildingTask.Host {

        private ClusterTransitionsBuildingTask task;

        ClusterTransitionsBuildingTaskHost() {
            this.task = new ClusterTransitionsBuildingTask(this);
        }

        @Override
        public void onClusterTransitionsBuildingTaskPostExecute(ClusterTransitionsBuildingTask.Result result) {
            ProcessingListener processingListener = options.getProcessingListener();
            if (processingListener != null) {
                processingListener.onClusteringFinished();
            }
            if (result != null) {
                transitionClusters(result.clusterTransitions);
            }
            task = null;
        }

        public void cancel() {
            task.cancel(true);
            task = null;
        }

        @SuppressLint("NewApi")
        public void executeTask(Projection projection) {
            if (projection != null) {
                ClusterTransitionsBuildingTask.Argument arg = new ClusterTransitionsBuildingTask.Argument();
                arg.currentClusters = currentClusters;
                arg.previousClusters = previousClusters;
                arg.projection = projection;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arg);
                } else {
                    task.execute(arg);
                }
            }
        }
    }

    public interface ProcessingListener {
        void onClusteringStarted();

        void onClusteringFinished();
    }
}
