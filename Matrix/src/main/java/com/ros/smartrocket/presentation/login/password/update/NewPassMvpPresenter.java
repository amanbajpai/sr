package com.ros.smartrocket.presentation.login.password.update;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface NewPassMvpPresenter<V extends NewPassMvpView> extends MvpPresenter<V> {
    void changePassword(String email, String token, String newPassword);
}
