package com.ros.smartrocket.presentation.account.base;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AccountPresenter<V extends AccountMvpView> extends BaseNetworkPresenter<V> implements AccountMvpPresenter<V> {
    private boolean showProgress;

    public AccountPresenter(boolean showProgress) {
        this.showProgress = showProgress;
    }

    @Override
    public void getAccount() {
        if (showProgress)
            showLoading(false);
        addDisposable(App.getInstance().getApi()
                .getMyAccount(getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeAccount)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::showNetworkError)
                .subscribe(this::handleAccountRetrieved, t->{}));
    }

    private void handleAccountRetrieved(MyAccount account) {
        if (showProgress)
            hideLoading();
        getMvpView().onAccountLoaded(account);
    }

    private void storeAccount(MyAccount account) {
        App.getInstance().setMyAccount(account);
    }
}
