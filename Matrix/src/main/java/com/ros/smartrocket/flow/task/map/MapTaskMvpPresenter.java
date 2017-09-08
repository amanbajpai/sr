package com.ros.smartrocket.flow.task.map;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.flow.task.TaskMvpPresenter;
import com.ros.smartrocket.flow.task.TaskMvpView;

public interface MapTaskMvpPresenter<V extends TaskMvpView> extends TaskMvpPresenter<V> {
    void loadTasksFromDb(int itemId, boolean isHidden, Keys.MapViewMode mode);
}
