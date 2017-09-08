package com.ros.smartrocket.flow.task;

import com.ros.smartrocket.flow.base.MvpPresenter;

public interface TaskMvpPresenter<V extends TaskMvpView> extends MvpPresenter<V> {
    void getMyTasksFromServer();
}
