package com.ros.smartrocket.flow.details.task;

import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.flow.base.NetworkMvpView;

interface TaskDetailsMvpView extends NetworkMvpView {
    void onTaskLoadedFromDb(Task task);

    void onWaveLoadedFromDb(Wave wave);

    void onTasksLoaded();
}
