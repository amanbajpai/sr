package com.ros.smartrocket.flow.settings;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface SettingsMvpPresenter<V extends SettingsMvpView> extends MvpPresenter<V> {
    void allowPushNotifications(boolean isAllowed);

    void closeAccount();
}
