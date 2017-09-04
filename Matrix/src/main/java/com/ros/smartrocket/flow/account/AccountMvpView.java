package com.ros.smartrocket.flow.account;

import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.flow.base.NetworkMvpView;

public interface AccountMvpView extends NetworkMvpView {
    void onAccountLoaded(MyAccount account);
}
