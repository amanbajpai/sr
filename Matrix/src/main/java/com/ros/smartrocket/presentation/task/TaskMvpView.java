package com.ros.smartrocket.presentation.task;

import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.presentation.base.RefreshIconMvpView;

import java.util.List;

public interface TaskMvpView extends RefreshIconMvpView {
    void onTaskLoadingComplete(List<Task> list);

    void onTasksLoaded();
}
