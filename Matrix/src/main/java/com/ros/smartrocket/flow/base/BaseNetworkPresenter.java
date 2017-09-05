package com.ros.smartrocket.flow.base;

import com.ros.smartrocket.net.NetworkError;

public class BaseNetworkPresenter<V extends NetworkMvpView> extends BasePresenter<V> {

    public void showNetworkError(Throwable t) {
        hideLoading();
        getMvpView().showNetworkError(new NetworkError(t));
    }

    public void showLoading(boolean isCancelable) {
        getMvpView().showLoading(isCancelable);
    }

    public void hideLoading() {
        getMvpView().hideLoading();
    }
}