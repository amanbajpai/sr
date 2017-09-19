package com.ros.smartrocket.presentation.login.promo;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class PromoPresenter<V extends PromoMvpView> extends BaseNetworkPresenter<V> implements PromoMvpPresenter<V> {

    @Override
    public void sendPromoCode(String code) {
        if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(getToken()))
            sendPromo(code);
        else
            getMvpView().paramsNotValid();
    }

    private void sendPromo(String code) {
        getMvpView().showLoading(false);
        addDisposable(App.getInstance().getApi()
                .setPromoCode(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> handleSuccess(), this::showNetworkError));
    }

    private void handleSuccess() {
        hideLoading();
        getMvpView().onPromoCodeSent();
    }

    private String getToken() {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        return TextUtils.isEmpty(preferencesManager.getTokenForUploadFile()) ?
                preferencesManager.getToken() :
                preferencesManager.getTokenForUploadFile();
    }
}
