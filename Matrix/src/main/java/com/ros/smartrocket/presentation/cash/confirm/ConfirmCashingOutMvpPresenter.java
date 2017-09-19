package com.ros.smartrocket.presentation.cash.confirm;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface ConfirmCashingOutMvpPresenter<V extends ConfirmCashingOutMvpView> extends MvpPresenter<V> {
    void cashingOut();
}
