package com.ros.smartrocket.ui.login.password;

import com.ros.smartrocket.ui.base.MvpPresenter;

interface PasswordMvpPresenter<V extends PasswordMvpView> extends MvpPresenter<V> {
    void login(String email, String password);
}
