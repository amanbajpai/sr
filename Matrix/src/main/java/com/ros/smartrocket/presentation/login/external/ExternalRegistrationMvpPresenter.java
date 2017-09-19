package com.ros.smartrocket.presentation.login.external;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface ExternalRegistrationMvpPresenter<V extends ExternalRegistrationMvpView> extends MvpPresenter<V> {

    void registerExternal(Long dob, String email);
}
