package com.ros.smartrocket.flow.cash.confirm;

import com.ros.smartrocket.flow.base.NetworkMvpView;

interface ConfirmCashingOutMvpView extends NetworkMvpView {
    void onCashOutSuccess();
}
