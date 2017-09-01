package com.ros.smartrocket.flow.login.password.forgot;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface ForgotPassMvpPresenter<V extends ForgotPassMvpView> extends MvpPresenter<V> {
    void restorePassword(String email);
}
