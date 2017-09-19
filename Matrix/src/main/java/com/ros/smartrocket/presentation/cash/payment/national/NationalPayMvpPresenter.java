package com.ros.smartrocket.presentation.cash.payment.national;

import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.presentation.base.MvpPresenter;

interface NationalPayMvpPresenter<V extends NationalPayMvpView> extends MvpPresenter<V> {
    void getNationalIdAccount();

    void integrateNationalIdAccount(NationalIdAccount nationalIdAccount);
}
