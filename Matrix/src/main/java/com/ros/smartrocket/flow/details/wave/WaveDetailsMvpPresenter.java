package com.ros.smartrocket.flow.details.wave;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface WaveDetailsMvpPresenter<V extends WaveDetailsMvpView> extends MvpPresenter<V> {

    void loadWaveWithNearTaskFromDB(Integer waveId);

    void setHideAllProjectTasksOnMapByID(Integer waveId, boolean shouldHide);

    void loadTaskFromDBbyID(Integer taskId, Integer missionId);
}
