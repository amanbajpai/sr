package com.ros.smartrocket.flow.settings;

import com.ros.smartrocket.flow.base.NetworkMvpView;

interface SettingsMvpView extends NetworkMvpView {
    void onAccountClosed();
    void onPushStatusChanged();
}
