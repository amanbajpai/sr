package com.ros.smartrocket.flow.wave.my;

import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.flow.wave.WaveMvpView;

import java.util.List;

interface MyWaveMvpView extends WaveMvpView {
    void onWavesLoadingComplete(List<Wave> list);
}
