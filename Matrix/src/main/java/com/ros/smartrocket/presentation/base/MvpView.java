package com.ros.smartrocket.presentation.base;

public interface MvpView {

    void showLoading(boolean isCancelable);

    void hideLoading();
}
