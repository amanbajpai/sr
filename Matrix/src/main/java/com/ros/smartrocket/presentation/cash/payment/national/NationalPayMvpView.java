package com.ros.smartrocket.presentation.cash.payment.national;

import com.ros.smartrocket.db.entity.account.NationalIdAccount;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface NationalPayMvpView extends NetworkMvpView {

    void onAccountLoaded(NationalIdAccount account);

    void onAccountIntegrated();

    void onFieldsEmpty();

}
