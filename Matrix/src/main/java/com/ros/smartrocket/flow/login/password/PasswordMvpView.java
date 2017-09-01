package com.ros.smartrocket.flow.login.password;

import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.LoginResponse;
import com.ros.smartrocket.flow.base.NetworkMvpView;

interface PasswordMvpView extends NetworkMvpView {

    void onPasswordFieldEmpty();

    Login getLoginEntity(String email, String password);

    void onLoginSuccess(LoginResponse response);

    boolean shouldStorePassword();
}
