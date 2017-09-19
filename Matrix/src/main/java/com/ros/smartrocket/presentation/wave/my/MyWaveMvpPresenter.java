package com.ros.smartrocket.presentation.wave.my;

import com.ros.smartrocket.presentation.wave.WaveMvpPresenter;

interface MyWaveMvpPresenter<V extends MyWaveMvpView> extends WaveMvpPresenter<V> {
    void loadNotMyWavesListFromDB(boolean isHidden);
}
