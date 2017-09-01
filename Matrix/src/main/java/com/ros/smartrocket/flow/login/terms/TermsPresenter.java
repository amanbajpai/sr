package com.ros.smartrocket.flow.login.terms;

import com.ros.smartrocket.App;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class TermsPresenter<V extends TermsMvpView> extends BaseNetworkPresenter<V> implements TermsMvpPresenter<V> {

    @Override
    public void sendTermsAndConditionsViewed() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .sendTandC()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> handleTaCSent(),
                        this::showNetworkError));
    }

    private void handleTaCSent() {
        hideLoading();
        getMvpView().onTermsAndConditionsSent();
    }
}
