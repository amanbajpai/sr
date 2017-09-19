package com.ros.smartrocket.presentation.details.wave;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface WaveDetailsMvpPresenter<V extends WaveDetailsMvpView> extends MvpPresenter<V> {

    void loadWaveWithNearTaskFromDB(Integer waveId);

    void setHideAllProjectTasksOnMapByID(Integer waveId, boolean shouldHide);

    void loadTaskFromDBbyID(Integer taskId, Integer missionId);
}
