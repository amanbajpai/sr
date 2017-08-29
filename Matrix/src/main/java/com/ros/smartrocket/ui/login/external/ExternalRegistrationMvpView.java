package com.ros.smartrocket.ui.login.external;

import com.ros.smartrocket.ui.base.NetworkMvpView;

interface ExternalRegistrationMvpView extends NetworkMvpView {
    void showDoBField();

    void showEmailField();

    void onFieldsEmpty();

    void onRegistrationSuccess(String email);
}
