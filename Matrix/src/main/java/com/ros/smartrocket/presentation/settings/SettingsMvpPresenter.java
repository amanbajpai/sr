package com.ros.smartrocket.presentation.settings;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface SettingsMvpPresenter<V extends SettingsMvpView> extends MvpPresenter<V> {
    void allowPushNotifications(boolean isAllowed);

    void closeAccount();
}
