package com.ros.smartrocket.flow.cash;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface ActivityMvpPresenter<V extends ActivityMvpView> extends MvpPresenter<V> {
    void sendActivity();
}
