package com.ros.smartrocket.flow.login.activate;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface ActivateAccMvpPresenter<V extends ActivateMvpView> extends MvpPresenter<V> {

    void activateAccount(String email, String token);
}
