package com.ros.smartrocket.map;

import android.location.Location;

import com.ros.smartrocket.map.location.MatrixLocationManager;

public abstract class CurrentLocatiuonListener implements MatrixLocationManager.GetCurrentLocationListener {
    @Override
    public void getLocationStart() {
        // not needed
    }

    @Override
    public void getLocationInProcess() {
        // not needed
    }

    @Override
    public abstract void getLocationSuccess(Location location);

    @Override
    public abstract void getLocationFail(String errorText);
}
