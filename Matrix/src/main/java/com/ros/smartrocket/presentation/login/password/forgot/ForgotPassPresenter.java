package com.ros.smartrocket.presentation.login.password.forgot;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.UIUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ForgotPassPresenter<V extends ForgotPassMvpView> extends BaseNetworkPresenter<V> implements ForgotPassMvpPresenter<V> {

    @Override
    public void restorePassword(String email) {
        if (checkEmail(email))
            forgotPassword(email);
        else
            getMvpView().onFieldsEmpty();
    }

    private void forgotPassword(String email) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .forgotPassword(email, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> handleSuccess(), this::showNetworkError));
    }

    private void handleSuccess() {
        hideLoading();
        getMvpView().onRequestSuccess();
    }

    private boolean checkEmail(String email) {
        return !TextUtils.isEmpty(email) && UIUtils.isEmailValid(email);
    }
}
