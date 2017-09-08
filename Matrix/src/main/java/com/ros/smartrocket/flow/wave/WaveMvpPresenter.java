package com.ros.smartrocket.flow.wave;

import com.ros.smartrocket.flow.base.MvpPresenter;

public interface WaveMvpPresenter<V extends WaveMvpView> extends MvpPresenter<V> {
    void getWavesFromServer(double latitude, double longitude, int radius);
}
