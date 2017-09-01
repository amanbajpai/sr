package com.ros.smartrocket.flow.login.location.failed;

import com.ros.smartrocket.flow.base.NetworkMvpView;

interface FailedLocationMvpView extends NetworkMvpView {

    void onLocationFailed();

    void onSubscriptionSuccess();
}
