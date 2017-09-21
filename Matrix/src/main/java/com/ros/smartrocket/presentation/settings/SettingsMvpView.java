package com.ros.smartrocket.presentation.settings;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface SettingsMvpView extends NetworkMvpView {
    void onAccountClosed();

    void onPushStatusChanged();
}
