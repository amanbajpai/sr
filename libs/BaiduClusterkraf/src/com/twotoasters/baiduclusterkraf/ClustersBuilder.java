package com.twotoasters.baiduclusterkraf;

import android.graphics.Point;
import android.view.Display;

import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

class ClustersBuilder {

    private final Options options;

    private final ArrayList<InputPoint> relevantInputPointsList = new ArrayList<InputPoint>();
    private final HashSet<InputPoint> releventInputPointsSet = new HashSet<InputPoint>();

    private WeakReference<Projection> projectionRef;
    private WeakReference<LatLngBounds> visibleRegionRef;

    ClustersBuilder(Projection projection, Options options, ArrayList<ClusterPoint> initialClusteredPoints) {
        this.options = options;

        Display display = options.getActivity().getWindowManager().getDefaultDisplay();

        if(projection!=null){
            LatLng leftTopLatLng = projection.fromScreenLocation(new Point(0, 0));
            LatLng rightTopLatLng = projection.fromScreenLocation(new Point(display.getWidth(), 0));
            LatLng leftBottomLatLng = projection.fromScreenLocation(new Point(0, display.getHeight()));
            LatLng rightBottomLatLng = projection.fromScreenLocation(new Point(display.getWidth(), display.getHeight()));


            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(leftTopLatLng)
                    .include(rightTopLatLng)
                    .include(leftBottomLatLng)
                    .include(rightBottomLatLng)
                    .build();

            this.projectionRef = new WeakReference<Projection>(projection);
            this.visibleRegionRef = new WeakReference<LatLngBounds>(bounds);
        } else {
            this.projectionRef = new WeakReference<Projection>(null);
            this.visibleRegionRef = new WeakReference<LatLngBounds>(null);
        }

        if (initialClusteredPoints != null) {
            addRelevantInitialInputPoints(initialClusteredPoints);
        }
    }

    private void addRelevantInitialInputPoints(ArrayList<ClusterPoint> initialClusteredPoints) {
        for (ClusterPoint clusterPoint : initialClusteredPoints) {
            clusterPoint.clearScreenPosition();
            addAll(clusterPoint.getPointsInCluster());
        }
    }

    private Projection getProjection() {
        return projectionRef.get();
    }

    private LatLngBounds getVisibleRegion() {
        return visibleRegionRef.get();
    }

    void addAll(ArrayList<InputPoint> points) {
        if (points != null) {
            Projection projection = getProjection();
            LatLngBounds visibleRegion = getVisibleRegion();
            if (projection != null && visibleRegion != null) {
                LatLngBounds bounds = getExpandedBounds(visibleRegion);
                for (InputPoint point : points) {
                    addIfNecessary(point, projection, bounds);
                }
            }
        }
    }

    private LatLngBounds getExpandedBounds(LatLngBounds bounds) {
        if (bounds != null && options != null) {
            double expandBoundsFactor = options.getExpandBoundsFactor();

            boolean spans180Meridian = bounds.northeast.longitude < bounds.southwest.longitude;

            double distanceFromNorthToSouth = bounds.northeast.latitude - bounds.southwest.latitude;
            double distanceFromEastToWest;
            if (spans180Meridian == false) {
                distanceFromEastToWest = bounds.northeast.longitude - bounds.southwest.longitude;
            } else {
                distanceFromEastToWest = (180 + bounds.northeast.longitude) + (180 - bounds.southwest.longitude);
            }

            double expandLatitude = distanceFromNorthToSouth * expandBoundsFactor;
            double expandLongitude = distanceFromEastToWest * expandBoundsFactor;

            LatLng newNortheast = new LatLng(bounds.northeast.latitude + expandLatitude, bounds.northeast.longitude + expandLongitude);
            LatLng newSouthwest = new LatLng(bounds.southwest.latitude - expandLatitude, bounds.southwest.longitude - expandLongitude);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(newSouthwest);
            builder.include(newNortheast);
            return builder.build();
        }
        return null;
    }

    private void addIfNecessary(InputPoint point, Projection projection, LatLngBounds bounds) {
        if (bounds != null && bounds.contains(point.getMapPosition()) && !releventInputPointsSet.contains(point)) {
            point.buildScreenPosition(projection);
            relevantInputPointsList.add(point);
            releventInputPointsSet.add(point);
        }
    }

    ArrayList<ClusterPoint> build() {
        Projection projection = getProjection();
        ArrayList<ClusterPoint> clusteredPoints = null;
        if (projection != null) {
            clusteredPoints = new ArrayList<ClusterPoint>(relevantInputPointsList.size());
            for (InputPoint point : relevantInputPointsList) {
                boolean addedToExistingCluster = false;
                for (ClusterPoint clusterPoint : clusteredPoints) {
                    if (clusterPoint.getPixelDistanceFrom(point) <= options.getPixelDistanceToJoinCluster()) {
                        clusterPoint.add(point);
                        addedToExistingCluster = true;
                        break;
                    }
                }
                if (addedToExistingCluster == false) {
                    clusteredPoints.add(new ClusterPoint(point, projection, false));
                }
            }
        }
        return clusteredPoints;
    }

}
