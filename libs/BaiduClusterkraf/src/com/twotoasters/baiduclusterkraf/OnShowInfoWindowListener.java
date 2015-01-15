package com.twotoasters.baiduclusterkraf;

import com.baidu.mapapi.map.Marker;

public interface OnShowInfoWindowListener {
	boolean onShowInfoWindow(Marker marker, ClusterPoint clusterPoint);
}
