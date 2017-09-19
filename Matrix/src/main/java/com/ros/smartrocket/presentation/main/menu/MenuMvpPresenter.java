package com.ros.smartrocket.presentation.main.menu;

import android.graphics.Bitmap;

import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface MenuMvpPresenter<V extends MenuMvpView> extends MvpPresenter<V> {
    void getMyTasksCount();

    void updateUserImage(Bitmap avatar);

    void updateUserName(String name);

    void getUnreadNotificationsCount();
}
