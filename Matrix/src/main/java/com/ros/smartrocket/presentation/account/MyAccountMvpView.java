package com.ros.smartrocket.presentation.account;

import com.ros.smartrocket.presentation.account.base.AccountMvpView;

public interface MyAccountMvpView extends AccountMvpView {
    void onUserImageUpdated();

    void onUserNameUpdated();

    void onUserUpdateFailed();
}
