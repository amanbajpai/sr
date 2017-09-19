package com.ros.smartrocket.presentation.task.my;

import com.ros.smartrocket.presentation.task.TaskMvpPresenter;
import com.ros.smartrocket.presentation.task.TaskMvpView;

interface MyTaskMvpPresenter<V extends TaskMvpView> extends TaskMvpPresenter<V> {
    void loadMyTasksFromDb();
}
