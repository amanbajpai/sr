package com.ros.smartrocket.presentation.wave.my;

import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.presentation.wave.WaveMvpView;

import java.util.List;

interface MyWaveMvpView extends WaveMvpView {
    void onWavesLoadingComplete(List<Wave> list);
}
