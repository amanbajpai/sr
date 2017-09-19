package com.ros.smartrocket.presentation.cash.confirm;

import com.ros.smartrocket.App;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ConfirmCashingOutPresenter<V extends ConfirmCashingOutMvpView> extends BaseNetworkPresenter<V> implements ConfirmCashingOutMvpPresenter<V> {

    @Override
    public void cashingOut() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .cashingOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> handleSuccess(), this::showNetworkError)
        );
    }

    private void handleSuccess() {
        hideLoading();
        getMvpView().onCashOutSuccess();
    }
}
