package com.ros.smartrocket.flow.login.password;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface PasswordMvpPresenter<V extends PasswordMvpView> extends MvpPresenter<V> {
    void login(String email, String password);
}
