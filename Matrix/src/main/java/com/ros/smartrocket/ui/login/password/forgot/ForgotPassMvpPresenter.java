package com.ros.smartrocket.ui.login.password.forgot;

import com.ros.smartrocket.ui.base.MvpPresenter;

interface ForgotPassMvpPresenter<V extends ForgotPassMvpView> extends MvpPresenter<V> {
    void restorePassword(String email);
}
