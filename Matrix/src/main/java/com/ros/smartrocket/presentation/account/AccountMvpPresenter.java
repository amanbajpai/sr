package com.ros.smartrocket.presentation.account;

import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface AccountMvpPresenter<V extends AccountMvpView>  extends MvpPresenter<V>{
    void getAccount();
}
