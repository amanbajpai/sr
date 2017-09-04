package com.ros.smartrocket.flow.cash;

import com.ros.smartrocket.App;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ActivityPresenter<V extends ActivityMvpView> extends BaseNetworkPresenter<V> implements ActivityMvpPresenter<V> {

    @Override
    public void sendActivity() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .sendActivity(getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> handleSuccess(), this::showNetworkError)
        );
    }

    private void handleSuccess() {
        hideLoading();
        getMvpView().onActivitySent();
    }
}
