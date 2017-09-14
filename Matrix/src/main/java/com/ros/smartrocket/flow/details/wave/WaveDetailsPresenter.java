package com.ros.smartrocket.flow.details.wave;

import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

public class WaveDetailsPresenter<V extends WaveDetailsMvpView> extends BaseNetworkPresenter<V> implements WaveDetailsMvpPresenter<V> {

    @Override
    public void loadWaveWithNearTaskFromDB(Integer waveId) {
        //WavesBL.getWaveWithNearTaskFromDB(handler, waveId);
    }

    @Override
    public void setHideAllProjectTasksOnMapByID(Integer waveId, boolean shouldHide) {
        //TaskBL.setHideAllProjectTasksOnMapByID
    }

    @Override
    public void getTaskFromDBbyID(Integer taskId, Integer waveId) {
        // getTaskFromDBbyIdObservable
    }
}
