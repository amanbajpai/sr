package com.ros.smartrocket.ui.login.location;

import com.ros.smartrocket.ui.base.MvpPresenter;

public interface CheckLocationMvpPresenter<V extends CheckLocationMvpView> extends MvpPresenter<V> {
    void checkLocation();
}
