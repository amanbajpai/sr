package com.ros.smartrocket.presentation.launch;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface LaunchMvpPresenter<V extends LaunchMvpView> extends MvpPresenter<V> {
    void checkVersion();
}
