package com.ros.smartrocket.presentation.login.password.forgot;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface ForgotPassMvpPresenter<V extends ForgotPassMvpView> extends MvpPresenter<V> {
    void restorePassword(String email);
}
