package com.ros.smartrocket.map;

import com.google.android.gms.maps.model.LatLng;
import com.twotoasters.clusterkraf.InputPoint;

import java.util.ArrayList;

public class BaiduClusterInputPoint extends InputPoint {
    private static final String TAG = "BaiduClusterPoint";

    private final ArrayList<InputPoint> pointsInClusterList = new ArrayList<InputPoint>();

    public BaiduClusterInputPoint(LatLng mapPosition) {
        super(mapPosition);
    }

    public BaiduClusterInputPoint(LatLng mapPosition, Object tag) {
        super(mapPosition, tag);
    }

    void add(InputPoint point) {
        pointsInClusterList.add(point);
    }

}
