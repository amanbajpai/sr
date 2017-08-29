package com.ros.smartrocket.ui.login.password;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.LoginResponse;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.base.BasePresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.helpers.WriteDataHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class PasswordPresenter<V extends PasswordMvpView> extends BasePresenter<V> implements PasswordMvpPresenter<V> {
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
        getMvpView().showLoading(false);

        Call<LoginResponse> call = App.getInstance().getApi().login(loginEntity);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                getMvpView().hideLoading();

                if (response.isSuccessful())
                    onLoginSuccess(response.body());
                else
                    getMvpView().showNetworkError(new NetworkError(response.errorBody()));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                getMvpView().hideLoading();
                getMvpView().showNetworkError(new NetworkError(t));
            }
        });
    }

    private void onLoginSuccess(LoginResponse response) {
        storeUserData(response);
        getMvpView().onLoginSuccess(response);
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
