package com.ros.smartrocket.presentation.login.password.update;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface NewPassMvpView extends NetworkMvpView {
    void passwordNotValid();

    void onPasswordChangeSuccess();
}
