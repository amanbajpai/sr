package com.ros.smartrocket.presentation.login.location;

import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface CheckLocationMvpPresenter<V extends CheckLocationMvpView> extends MvpPresenter<V> {
    void checkLocation();
}
