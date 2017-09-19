package com.ros.smartrocket.presentation.main.menu;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

public interface MenuMvpView extends NetworkMvpView {
    void setUnreadNotificationsCount(int count);

    void setMyTasksCount(int count);

    void onUserImageUpdated();

    void onUserNameUpdated();

    void onUserUpdateFailed();

}
