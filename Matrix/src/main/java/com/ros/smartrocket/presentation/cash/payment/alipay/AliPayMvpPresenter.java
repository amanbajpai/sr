package com.ros.smartrocket.presentation.cash.payment.alipay;

import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.presentation.base.MvpPresenter;

interface AliPayMvpPresenter<V extends AliPayMvpView> extends MvpPresenter<V> {
    void getAliPayAccount();
    void integrateAliPayAccount(AliPayAccount account);
}
