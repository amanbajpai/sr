package com.ros.smartrocket.presentation.main.menu;

import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface MenuMvpPresenter<V extends MenuMvpView> extends MvpPresenter<V> {
    void getMyTasksCount();

    void getUnreadNotificationsCount();
}
