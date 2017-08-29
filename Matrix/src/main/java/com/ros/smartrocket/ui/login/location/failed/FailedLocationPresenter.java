package com.ros.smartrocket.ui.login.location.failed;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.base.BasePresenter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


class FailedLocationPresenter<V extends FailedLocationMvpView> extends BasePresenter<V> implements FailedLocationMvpPresenter<V> {

    @Override
    public void subscribe(Subscription subscription) {
        if (isLocationValid(subscription) && isEmailValid(subscription.getEmail()))
            subscribeUser(subscription);
    }

    private void subscribeUser(Subscription subscription) {
        getMvpView().showLoading(false);
        Call<ResponseBody> call = App.getInstance().getApi().subscribe(subscription);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isViewAttached()) {
                    if (response.isSuccessful())
                        getMvpView().onSubscriptionSuccess();
                    else
                        getMvpView().showNetworkError(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    getMvpView().showNetworkError(new NetworkError(t));
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email);
    }

    private boolean isLocationValid(Subscription subscription) {
        if (subscription.getLatitude() != null && subscription.getLongitude() != null) {
            return true;
        } else {
            getMvpView().onLocationFailed();
            return false;
        }
    }
}
