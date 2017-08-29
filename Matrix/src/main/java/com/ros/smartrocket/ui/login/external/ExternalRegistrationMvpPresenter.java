package com.ros.smartrocket.ui.login.external;

import com.ros.smartrocket.ui.base.MvpPresenter;

interface ExternalRegistrationMvpPresenter<V extends ExternalRegistrationMvpView> extends MvpPresenter<V> {

    void registerExternal(Long dob, String email);
}
