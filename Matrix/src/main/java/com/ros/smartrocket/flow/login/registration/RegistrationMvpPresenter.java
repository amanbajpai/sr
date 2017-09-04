package com.ros.smartrocket.flow.login.registration;

import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.flow.base.MvpPresenter;

interface RegistrationMvpPresenter<V extends RegistrationMvpView> extends MvpPresenter<V> {

    void register(Registration registration);
}
