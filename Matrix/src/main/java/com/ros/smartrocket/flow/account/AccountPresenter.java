package com.ros.smartrocket.flow.account;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AccountPresenter<V extends AccountMvpView> extends BaseNetworkPresenter<V> implements AccountMvpPresenter<V> {
    @Override
    public void getAccount() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .getMyAccount(getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeAccount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleAccountRetrieved, this::showNetworkError));
    }

    private void handleAccountRetrieved(MyAccount account) {
        hideLoading();
        getMvpView().onAccountLoaded(account);
    }

    private void storeAccount(MyAccount account) {
        App.getInstance().setMyAccount(account);
    }
}
