package com.ros.smartrocket.presentation.login.activate;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface ActivateAccMvpPresenter<V extends ActivateMvpView> extends MvpPresenter<V> {

    void activateAccount(String email, String token);
}
