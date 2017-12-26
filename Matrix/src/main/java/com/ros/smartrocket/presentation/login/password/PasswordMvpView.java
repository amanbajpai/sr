package com.ros.smartrocket.presentation.login.password;

import com.ros.smartrocket.db.entity.account.Login;
import com.ros.smartrocket.db.entity.account.LoginResponse;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface PasswordMvpView extends NetworkMvpView {

    void onPasswordFieldEmpty();

    Login getLoginEntity(String email, String password);

    void onLoginSuccess(LoginResponse response);

    boolean shouldStorePassword();
}
