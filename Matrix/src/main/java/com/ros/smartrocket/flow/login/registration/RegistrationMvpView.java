package com.ros.smartrocket.flow.login.registration;

import com.ros.smartrocket.flow.base.NetworkMvpView;

interface RegistrationMvpView extends NetworkMvpView {
    void notValidEmail();

    void notValidName();

    void notValidPassword();

    void notValidBirthday();

    void notValidGender();

    void onRegistrationSuccess();
}
