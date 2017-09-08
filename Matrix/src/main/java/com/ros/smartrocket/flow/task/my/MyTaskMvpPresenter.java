package com.ros.smartrocket.flow.task.my;

import com.ros.smartrocket.flow.task.TaskMvpPresenter;
import com.ros.smartrocket.flow.task.TaskMvpView;

interface MyTaskMvpPresenter<V extends TaskMvpView> extends TaskMvpPresenter<V> {
    void loadMyTasksFromDb();
}
