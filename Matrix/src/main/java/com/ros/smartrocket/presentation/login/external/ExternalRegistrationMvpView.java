package com.ros.smartrocket.presentation.login.external;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface ExternalRegistrationMvpView extends NetworkMvpView {
    void showDoBField();

    void showEmailField();

    void onFieldsEmpty();

    void onRegistrationSuccess(String email);
}
