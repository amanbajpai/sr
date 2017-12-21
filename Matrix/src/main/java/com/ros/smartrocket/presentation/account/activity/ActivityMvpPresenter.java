package com.ros.smartrocket.presentation.account.activity;

import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface ActivityMvpPresenter<V extends ActivityMvpView> extends MvpPresenter<V> {
    void sendActivity();
}
