package com.ros.smartrocket.flow.account;

import com.ros.smartrocket.flow.base.MvpPresenter;

public interface AccountMvpPresenter<V extends AccountMvpView>  extends MvpPresenter<V>{
    void getAccount();
}
