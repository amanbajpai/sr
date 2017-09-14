package com.ros.smartrocket.flow.details.task;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface TaskDetailMvpPresenter<V extends TaskDetailsMvpView> extends MvpPresenter<V> {
    void setHideTaskOnMapByID(Integer taskId, Integer missionId, boolean shouldHide);

    void loadTaskFromDBbyID(Integer taskId, Integer missionId);

    void loadWaveFromDB(Integer waveId);

    void getMyTasksFromServer();
}
