package com.ros.smartrocket.presentation.login.location.failed;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface FailedLocationMvpView extends NetworkMvpView {

    void onLocationFailed();

    void onSubscriptionSuccess();
}
