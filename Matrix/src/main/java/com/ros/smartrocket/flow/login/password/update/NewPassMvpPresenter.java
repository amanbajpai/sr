package com.ros.smartrocket.flow.login.password.update;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface NewPassMvpPresenter<V extends NewPassMvpView> extends MvpPresenter<V> {
    void changePassword(String email, String token, String newPassword);
}
