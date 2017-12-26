package com.ros.smartrocket.presentation.account.base;

import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

public interface AccountMvpView extends NetworkMvpView {
    void onAccountLoaded(MyAccount account);
}
