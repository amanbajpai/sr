package com.ros.smartrocket.flow.cash.payment.alipay;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.utils.ValidationUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class AliPayPresenter<V extends AliPayMvpView> extends BaseNetworkPresenter<V> implements AliPayMvpPresenter<V> {
    @Override
    public void getAliPayAccount() {
        getMvpView().startProgress();
        addDisposable(App.getInstance().getApi()
                .getAliPayAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleAccount, this::showNetworkError));
    }

    private void handleAccount(AliPayAccount account) {
        getMvpView().clearProgress();
        getMvpView().onAccountLoaded(account);
    }

    @Override
    public void integrateAliPayAccount(AliPayAccount account) {
        if (isAccountValid(account))
            integrateAccount(account);
    }

    private void integrateAccount(AliPayAccount account) {
        getMvpView().startProgress();
        addDisposable(App.getInstance().getApi()
                .integrateAliPayAccount(account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> handleAccountIntegratedSuccess(), this::showNetworkError));
    }

    private void handleAccountIntegratedSuccess() {
        MyAccount myAccount = App.getInstance().getMyAccount();
        myAccount.setIsPaymentAccountExists(true);
        App.getInstance().setMyAccount(myAccount);
        getMvpView().clearProgress();
        getMvpView().onAccountIntegrated();
    }

    private boolean isAccountValid(AliPayAccount account) {
        boolean ok = true;
        if (TextUtils.isEmpty(account.getAccName())) {
            getMvpView().notValidName();
            ok = false;
        }
        if (!(ValidationUtils.validEmail(account.getUserId())
                || ValidationUtils.validChinaPhone(account.getUserId()))) {
            getMvpView().notValidUserId();
            ok = false;
        }
        return ok;

    }

    @Override
    public void showNetworkError(Throwable t) {
        getMvpView().clearProgress();
        getMvpView().showNetworkError(new NetworkError(t));
    }
}
