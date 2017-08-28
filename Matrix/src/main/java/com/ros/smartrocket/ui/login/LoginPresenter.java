package com.ros.smartrocket.ui.login;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.CheckEmail;
import com.ros.smartrocket.db.entity.ErrorResponse;
import com.ros.smartrocket.db.entity.ExternalAuthResponse;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.base.BasePresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class LoginPresenter<V extends LoginMvpView> extends BasePresenter<V> implements LoginMvpPresenter<V> {
    private String email;

    @Override
    public void checkEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            if (UIUtils.isAllFilesSend(email)) {
                this.email = email;
                checkUsersEmail();
            } else {
                getMvpView().onNotAllFilesSent();
            }
        } else {
            getMvpView().onEmailFieldEmpty();
        }
    }

    @Override
    public void externalAuth(ExternalAuthorize authorize) {
        getMvpView().showLoading(false);
        Call<ExternalAuthResponse> call = App.getInstance()
                .getApi().externalAuth(authorize, PreferencesManager.getInstance().getLanguageCode());
        call.enqueue(new Callback<ExternalAuthResponse>() {
            @Override
            public void onResponse(Call<ExternalAuthResponse> call, Response<ExternalAuthResponse> response) {
                getMvpView().hideLoading();
                if (response.isSuccessful()) {
                    PreferencesManager.getInstance().setLastEmail(authorize.getEmail());
                    storeUserData(response.body());
                    getMvpView().onExternalAuth(response.body());
                } else {
                    handleSpecificAuthError(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ExternalAuthResponse> call, Throwable t) {
                getMvpView().hideLoading();
                getMvpView().showNetworkError(new NetworkError(t));
            }
        });
    }

    private void storeUserData(ExternalAuthResponse authResponse) {
        PreferencesManager pm = PreferencesManager.getInstance();
        pm.setToken(authResponse.getToken());
        pm.setTokenForUploadFile(authResponse.getToken());
        pm.setTokenUpdateDate(System.currentTimeMillis());
    }

    private void handleSpecificAuthError(ResponseBody responseBody) {
        NetworkError networkError = new NetworkError(responseBody);
        ErrorResponse errorResponse = networkError.getErrorResponse();
        if (errorResponse != null && errorResponse.getData() != null) {
            int registrationBitMask = errorResponse.getData().getMissingFields();
            getMvpView().startRegistrationFlow(RegistrationType.SOCIAL_ADDITIONAL_INFO, registrationBitMask);
        } else {
            getMvpView().showNetworkError(networkError);
        }
    }

    private void checkUsersEmail() {
        getMvpView().showLoading(false);
        try {
            email = URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Call<CheckEmail> call = App.getInstance().getApi().checkEmail(email);
        call.enqueue(new Callback<CheckEmail>() {
            @Override
            public void onResponse(Call<CheckEmail> call, Response<CheckEmail> response) {
                getMvpView().hideLoading();
                if (response.isSuccessful()) {
                    CheckEmail checkEmail = response.body();
                    handleCheckEmailResponse(checkEmail);
                } else {
                    getMvpView().showNetworkError(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<CheckEmail> call, Throwable t) {
                getMvpView().hideLoading();
                getMvpView().showNetworkError(new NetworkError(t));
            }
        });
    }

    private void handleCheckEmailResponse(CheckEmail checkEmail) {
        if (checkEmail.isEmailExists()) {
            getMvpView().onEmailExist(email);
        } else {
            getMvpView().startRegistrationFlow(RegistrationType.NORMAL, -1);
        }
    }
}
