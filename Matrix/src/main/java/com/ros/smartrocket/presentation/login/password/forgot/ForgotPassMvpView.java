package com.ros.smartrocket.presentation.login.password.forgot;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface ForgotPassMvpView extends NetworkMvpView {
    void onRequestSuccess();

    void onFieldsEmpty();
}
