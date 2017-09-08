package com.ros.smartrocket.flow.task.mvp;

import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.RefreshIconMvpView;

import java.util.List;

public interface TaskMvpView extends RefreshIconMvpView {
    void onTaskLoadingComplete(List<Task> list);
    void onTasksLoaded();
}
