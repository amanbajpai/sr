package com.ros.smartrocket.ui.login.password.forgot;

import com.ros.smartrocket.ui.base.NetworkMvpView;

interface ForgotPassMvpView extends NetworkMvpView {
    void onRequestSuccess();

    void onFieldsEmpty();
}
