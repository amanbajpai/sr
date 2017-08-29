package com.ros.smartrocket.ui.login.location.failed;

import com.ros.smartrocket.ui.base.NetworkMvpView;

interface FailedLocationMvpView extends NetworkMvpView {

    void onLocationFailed();

    void onSubscriptionSuccess();
}
