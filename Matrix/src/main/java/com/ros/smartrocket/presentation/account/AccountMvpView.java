package com.ros.smartrocket.presentation.account;

import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

public interface AccountMvpView extends NetworkMvpView {
    void onAccountLoaded(MyAccount account);
}
