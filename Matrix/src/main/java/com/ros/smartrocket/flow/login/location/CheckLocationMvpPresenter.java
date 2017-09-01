package com.ros.smartrocket.flow.login.location;

import com.ros.smartrocket.flow.base.MvpPresenter;

public interface CheckLocationMvpPresenter<V extends CheckLocationMvpView> extends MvpPresenter<V> {
    void checkLocation();
}
