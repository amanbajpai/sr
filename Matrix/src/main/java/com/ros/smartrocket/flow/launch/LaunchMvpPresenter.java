package com.ros.smartrocket.flow.launch;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface LaunchMvpPresenter<V extends LaunchMvpView> extends MvpPresenter<V> {
    void checkVersion();
}
