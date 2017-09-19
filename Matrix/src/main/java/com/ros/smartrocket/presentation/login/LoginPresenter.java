package com.ros.smartrocket.presentation.login;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.CheckEmail;
import com.ros.smartrocket.db.entity.ErrorResponse;
import com.ros.smartrocket.db.entity.ExternalAuthResponse;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

class LoginPresenter<V extends LoginMvpView> extends BaseNetworkPresenter<V> implements LoginMvpPresenter<V> {
    private String email;
    private String externalAuthEmail;

    @Override
    public void checkEmail(String email) {
        if (isEmailValid(email)) {
            if (UIUtils.isAllFilesSend(email)) {
                this.email = email;
                checkUsersEmail();
            } else {
                getMvpView().onNotAllFilesSent();
            }
        }
    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            getMvpView().onEmailFieldEmpty();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void externalAuth(ExternalAuthorize authorize) {
        externalAuthEmail = authorize.getEmail();
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .externalAuth(authorize, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleExternalAuthResponse, this::showNetworkError));
    }

    private void checkUsersEmail() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .checkEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleCheckEmailResponse, this::showNetworkError)
        );
    }

    private void handleCheckEmailResponse(CheckEmail checkEmail) {
        hideLoading();
        if (checkEmail.isEmailExists())
            getMvpView().onEmailExist(email);
        else
            getMvpView().startRegistrationFlow(RegistrationType.NORMAL, -1);
    }

    private void handleExternalAuthResponse(Response<ExternalAuthResponse> response) {
        hideLoading();
        if (response.isSuccessful()) {
            PreferencesManager.getInstance().setLastEmail(externalAuthEmail);
            storeUserData(response.body());
            getMvpView().onExternalAuth(response.body());
        } else {
            handleSpecificAuthError(response.errorBody());
        }
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

    private void storeUserData(ExternalAuthResponse authResponse) {
        PreferencesManager pm = PreferencesManager.getInstance();
        pm.setToken(authResponse.getToken());
        pm.setTokenForUploadFile(authResponse.getToken());
        pm.setTokenUpdateDate(System.currentTimeMillis());
    }

}
