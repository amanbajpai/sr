package com.ros.smartrocket.flow.cash.payment.alipay;

import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.flow.base.NetworkMvpView;

interface AliPayMvpView extends NetworkMvpView {
    void startProgress();

    void clearProgress();

    void notValidName();

    void notValidUserId();

    void onAccountLoaded(AliPayAccount account);

    void onAccountIntegrated();
}
