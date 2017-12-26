package com.ros.smartrocket.presentation.details.wave;

import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.Wave;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface WaveDetailsMvpView extends NetworkMvpView {

    void onTasksHided();

    void onTasksUnHided();

    void onWaveLoadedFromDb(Wave w);

    void onNearTaskLoadedFromDb(Task task);
}
