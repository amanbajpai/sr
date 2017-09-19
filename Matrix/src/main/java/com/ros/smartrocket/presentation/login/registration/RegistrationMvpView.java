package com.ros.smartrocket.presentation.login.registration;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface RegistrationMvpView extends NetworkMvpView {
    void notValidEmail();

    void notValidName();

    void notValidPassword();

    void notValidBirthday();

    void notValidGender();

    void onRegistrationSuccess();
}
