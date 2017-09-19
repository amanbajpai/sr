package com.ros.smartrocket.presentation.task;

import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface TaskMvpPresenter<V extends TaskMvpView> extends MvpPresenter<V> {
    void getMyTasksFromServer();
}
