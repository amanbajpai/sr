package com.ros.smartrocket.presentation.login.password;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface PasswordMvpPresenter<V extends PasswordMvpView> extends MvpPresenter<V> {
    void login(String email, String password);
}
