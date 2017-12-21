package com.ros.smartrocket.presentation.account;

import android.graphics.Bitmap;

import com.ros.smartrocket.presentation.account.base.AccountMvpPresenter;

public interface MyAccountMvpPresenter<V extends MyAccountMvpView> extends AccountMvpPresenter<V> {
    void updateUserImage(Bitmap avatar);

    void updateUserName(String name);
}
