package com.ros.smartrocket.flow.wave.my;

import com.ros.smartrocket.flow.wave.WaveMvpPresenter;

interface MyWaveMvpPresenter<V extends MyWaveMvpView> extends WaveMvpPresenter<V> {
    void loadNotMyWavesListFromDB(boolean isHidden);
}
