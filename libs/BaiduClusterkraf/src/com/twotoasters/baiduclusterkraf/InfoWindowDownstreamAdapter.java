package com.twotoasters.baiduclusterkraf;

import android.view.View;

import com.google.android.gms.maps.model.Marker;

public interface InfoWindowDownstreamAdapter {

    public View getInfoContents(Marker marker, ClusterPoint clusterPoint);

    public View getInfoWindow(Marker marker, ClusterPoint clusterPoint);
}
