package com.ros.smartrocket.presentation.login.activate;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.ActivateAccount;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ActivateAccPresenter<V extends ActivateMvpView> extends BaseNetworkPresenter<V> implements ActivateAccMvpPresenter<V> {

    @Override
    public void activateAccount(String email, String token) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .activateAccount(new ActivateAccount(email, token))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> handleResponse(), this::showNetworkError));
    }

    private void handleResponse() {
        hideLoading();
        getMvpView().onAccountActivated();
    }
}
