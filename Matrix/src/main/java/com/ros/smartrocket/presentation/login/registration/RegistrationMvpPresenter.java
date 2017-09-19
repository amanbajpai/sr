package com.ros.smartrocket.presentation.login.registration;

import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.presentation.base.MvpPresenter;

interface RegistrationMvpPresenter<V extends RegistrationMvpView> extends MvpPresenter<V> {

    void register(Registration registration);
}
