package com.ros.smartrocket.presentation.cash;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface ActivityMvpPresenter<V extends ActivityMvpView> extends MvpPresenter<V> {
    void sendActivity();
}
