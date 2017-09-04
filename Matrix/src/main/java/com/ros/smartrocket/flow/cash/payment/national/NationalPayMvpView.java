package com.ros.smartrocket.flow.cash.payment.national;

import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.flow.base.NetworkMvpView;

interface NationalPayMvpView extends NetworkMvpView {

    void onAccountLoaded(NationalIdAccount account);

    void onAccountIntegrated();

    void onFieldsEmpty();

}
