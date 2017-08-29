package com.ros.smartrocket.ui.login.location.failed;

import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.ui.base.MvpPresenter;

interface FailedLocationMvpPresenter<V extends FailedLocationMvpView> extends MvpPresenter<V> {
    void subscribe(Subscription subscription);
}
