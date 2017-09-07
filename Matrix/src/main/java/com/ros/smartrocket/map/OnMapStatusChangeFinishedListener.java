package com.ros.smartrocket.map;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;

public abstract class OnMapStatusChangeFinishedListener implements BaiduMap.OnMapStatusChangeListener {
    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
        // not needed
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
        // not needed
    }

    @Override
    public abstract void onMapStatusChangeFinish(MapStatus mapStatus);
}
