package com.ros.smartrocket.flow.login.password.update;

import com.ros.smartrocket.flow.base.NetworkMvpView;

interface NewPassMvpView extends NetworkMvpView {
    void passwordNotValid();

    void onPasswordChangeSuccess();
}
