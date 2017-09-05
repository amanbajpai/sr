package com.ros.smartrocket.flow.map;

import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.NetworkMvpView;

import java.util.List;

interface MapMvpView extends NetworkMvpView {
    void refreshIconState(boolean isLoading);

    void onWavesLoaded();

    void onTaskLoadingComplete(List<Task> list);
}
