package com.ros.smartrocket.flow.map;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.flow.base.MvpPresenter;

interface MapMvpPresenter<V extends MapMvpView> extends MvpPresenter<V> {
    void getMyTasksFromServer();

    void getWavesFromServer(double latitude, double longitude, int radius);

    void loadTasksFromDb(int itemId, boolean isHidden, Keys.MapViewMode mode);
}
