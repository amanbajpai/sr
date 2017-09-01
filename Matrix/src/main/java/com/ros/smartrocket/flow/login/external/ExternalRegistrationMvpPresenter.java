package com.ros.smartrocket.flow.login.external;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface ExternalRegistrationMvpPresenter<V extends ExternalRegistrationMvpView> extends MvpPresenter<V> {

    void registerExternal(Long dob, String email);
}
