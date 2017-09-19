package com.ros.smartrocket.presentation.login.location.failed;

import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.presentation.base.MvpPresenter;

interface FailedLocationMvpPresenter<V extends FailedLocationMvpView> extends MvpPresenter<V> {
    void subscribe(Subscription subscription);
}
