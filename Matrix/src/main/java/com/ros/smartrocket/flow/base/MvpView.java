package com.ros.smartrocket.flow.base;

public interface MvpView {

    void showLoading(boolean isCancelable);

    void hideLoading();
}
