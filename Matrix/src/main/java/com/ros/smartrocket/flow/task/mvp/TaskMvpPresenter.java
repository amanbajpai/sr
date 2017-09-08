package com.ros.smartrocket.flow.task.mvp;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.flow.base.MvpPresenter;

public interface TaskMvpPresenter<V extends TaskMvpView> extends MvpPresenter<V> {
    void getMyTasksFromServer();
    void loadTasksFromDb(int itemId, boolean isHidden, Keys.MapViewMode mode);
}
