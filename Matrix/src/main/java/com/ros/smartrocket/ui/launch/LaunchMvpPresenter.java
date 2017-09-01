package com.ros.smartrocket.ui.launch;

import com.ros.smartrocket.ui.base.MvpPresenter;

interface LaunchMvpPresenter<V extends LaunchMvpView> extends MvpPresenter<V> {
    void checkVersion();
}
