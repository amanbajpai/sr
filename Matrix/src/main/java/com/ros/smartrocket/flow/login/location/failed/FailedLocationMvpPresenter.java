package com.ros.smartrocket.flow.login.location.failed;

import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.flow.base.MvpPresenter;

interface FailedLocationMvpPresenter<V extends FailedLocationMvpView> extends MvpPresenter<V> {
    void subscribe(Subscription subscription);
}
