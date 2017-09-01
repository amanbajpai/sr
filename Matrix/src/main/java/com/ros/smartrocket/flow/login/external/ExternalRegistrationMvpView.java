package com.ros.smartrocket.flow.login.external;

import com.ros.smartrocket.flow.base.NetworkMvpView;

interface ExternalRegistrationMvpView extends NetworkMvpView {
    void showDoBField();

    void showEmailField();

    void onFieldsEmpty();

    void onRegistrationSuccess(String email);
}
