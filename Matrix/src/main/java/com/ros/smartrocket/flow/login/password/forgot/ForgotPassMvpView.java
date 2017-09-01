package com.ros.smartrocket.flow.login.password.forgot;

import com.ros.smartrocket.flow.base.NetworkMvpView;

interface ForgotPassMvpView extends NetworkMvpView {
    void onRequestSuccess();

    void onFieldsEmpty();
}
