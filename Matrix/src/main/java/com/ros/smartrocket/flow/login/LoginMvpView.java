package com.ros.smartrocket.flow.login;

import com.ros.smartrocket.db.entity.ExternalAuthResponse;
import com.ros.smartrocket.flow.base.NetworkMvpView;
import com.ros.smartrocket.utils.RegistrationType;

interface LoginMvpView extends NetworkMvpView {
    void onEmailExist(String email);

    void onEmailFieldEmpty();

    void onNotAllFilesSent();

    void startRegistrationFlow(RegistrationType type, int registrationBitMask);

    void onExternalAuth(ExternalAuthResponse response);

}