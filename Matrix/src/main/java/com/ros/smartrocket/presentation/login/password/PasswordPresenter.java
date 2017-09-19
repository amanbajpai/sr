package com.ros.smartrocket.presentation.login.password;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.LoginResponse;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.helpers.WriteDataHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class PasswordPresenter<V extends PasswordMvpView> extends BaseNetworkPresenter<V> implements PasswordMvpPresenter<V> {
    private String email;
    private String password;

    @Override
    public void login(String email, String password) {
        this.email = email;
        this.password = password;

        if (TextUtils.isEmpty(password))
            getMvpView().onPasswordFieldEmpty();
        else
            loginUser(getMvpView().getLoginEntity(email, password));
    }

    private void loginUser(Login loginEntity) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .login(loginEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeUserData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleLoginSuccess, this::showNetworkError));
    }

    private void handleLoginSuccess(LoginResponse loginResponse) {
        hideLoading();
        getMvpView().onLoginSuccess(loginResponse);
    }


    private void storeUserData(LoginResponse response) {
        WriteDataHelper.prepareLogin(App.getInstance(), email);
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        preferencesManager.setLastEmail(email);
        preferencesManager.setToken(response.getToken());
        preferencesManager.setTokenForUploadFile(response.getToken());
        preferencesManager.setTokenUpdateDate(System.currentTimeMillis());

        if (getMvpView().shouldStorePassword())
            preferencesManager.setLastPassword(password);
        else
            preferencesManager.setLastPassword("");
    }

}
