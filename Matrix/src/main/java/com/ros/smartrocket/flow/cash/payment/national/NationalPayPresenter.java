package com.ros.smartrocket.flow.cash.payment.national;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.utils.ValidationUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class NationalPayPresenter<V extends NationalPayMvpView> extends BaseNetworkPresenter<V> implements NationalPayMvpPresenter<V> {

    @Override
    public void getNationalIdAccount() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .getNationalIdAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleAccount, this::showNetworkError));
    }

    @Override
    public void integrateNationalIdAccount(NationalIdAccount nationalIdAccount) {
        if (isAccountValid(nationalIdAccount))
            integrateAccount(nationalIdAccount);
        else
            getMvpView().onFieldsEmpty();
    }

    private void handleAccount(NationalIdAccount account) {
        hideLoading();
        getMvpView().onAccountLoaded(account);
    }

    private void integrateAccount(NationalIdAccount account) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .integrateNationalPayAccount(account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> handleAccountIntegratedSuccess(), this::showNetworkError));
    }

    private void handleAccountIntegratedSuccess() {
        MyAccount myAccount = App.getInstance().getMyAccount();
        myAccount.setIsPaymentAccountExists(true);
        App.getInstance().setMyAccount(myAccount);
        hideLoading();
        getMvpView().onAccountIntegrated();
    }

    private boolean isAccountValid(NationalIdAccount account) {
        return !TextUtils.isEmpty(account.getName())
                && !TextUtils.isEmpty(account.getNationalId())
                && !TextUtils.isEmpty(account.getPhoneNumber());

    }
}
