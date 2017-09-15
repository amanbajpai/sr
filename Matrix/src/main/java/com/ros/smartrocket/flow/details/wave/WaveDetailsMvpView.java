package com.ros.smartrocket.flow.details.wave;

import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.flow.base.NetworkMvpView;

interface WaveDetailsMvpView extends NetworkMvpView {

    void onTasksHided();

    void onTasksUnHided();

    void onWaveLoadedFromDb(Wave w);

    void onNearTaskLoadedFromDb(Task task);
}
