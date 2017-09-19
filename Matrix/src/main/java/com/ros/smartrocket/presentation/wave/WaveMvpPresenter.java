package com.ros.smartrocket.presentation.wave;

import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface WaveMvpPresenter<V extends WaveMvpView> extends MvpPresenter<V> {
    void getWavesFromServer(double latitude, double longitude, int radius);
}
