package com.ros.smartrocket.flow.login.location.failed;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


class FailedLocationPresenter<V extends FailedLocationMvpView> extends BaseNetworkPresenter<V> implements FailedLocationMvpPresenter<V> {

    @Override
    public void subscribe(Subscription subscription) {
        if (isLocationValid(subscription) && isEmailValid(subscription.getEmail()))
            subscribeUser(subscription);
    }

    private void subscribeUser(Subscription subscription) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .subscribe(subscription)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> onSubscriptionSuccess(), this::showNetworkError)
        );
    }

    private void onSubscriptionSuccess() {
        hideLoading();
        getMvpView().onSubscriptionSuccess();
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email);
    }

    private boolean isLocationValid(Subscription subscription) {
        if (subscription.getLatitude() != null && subscription.getLongitude() != null)
            return true;
        else
            getMvpView().onLocationFailed();
        return false;
    }
}
