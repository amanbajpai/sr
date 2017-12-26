package com.ros.smartrocket.presentation.cash.payment.alipay;

import com.ros.smartrocket.db.entity.account.AliPayAccount;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface AliPayMvpView extends NetworkMvpView {
    void startProgress();

    void clearProgress();

    void notValidName();

    void notValidUserId();

    void onAccountLoaded(AliPayAccount account);

    void onAccountIntegrated();
}
