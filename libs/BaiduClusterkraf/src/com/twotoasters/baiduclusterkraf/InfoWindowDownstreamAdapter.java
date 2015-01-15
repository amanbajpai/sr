package com.twotoasters.baiduclusterkraf;

import android.view.View;

import com.baidu.mapapi.map.Marker;

public interface InfoWindowDownstreamAdapter {

    public View getInfoContents(Marker marker, ClusterPoint clusterPoint);

    public View getInfoWindow(Marker marker, ClusterPoint clusterPoint);
}
