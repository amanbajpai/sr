package com.ros.smartrocket.flow.cash.confirm;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface ConfirmCashingOutMvpPresenter<V extends ConfirmCashingOutMvpView> extends MvpPresenter<V> {
    void cashingOut();
}
