package com.ros.smartrocket.presentation.base;

interface MvpView {

    void showLoading(boolean isCancelable);

    void hideLoading();
}
