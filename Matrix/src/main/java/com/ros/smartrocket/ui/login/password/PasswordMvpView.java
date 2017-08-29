package com.ros.smartrocket.ui.login.password;

import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.LoginResponse;
import com.ros.smartrocket.ui.base.NetworkMvpView;

interface PasswordMvpView extends NetworkMvpView {

    void onPasswordFieldEmpty();

    Login getLoginEntity(String email, String password);

    void onLoginSuccess(LoginResponse response);

    boolean shouldStorePassword();
}
