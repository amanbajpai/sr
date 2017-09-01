package com.ros.smartrocket.flow.login;

import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.flow.base.MvpPresenter;

interface LoginMvpPresenter<V extends LoginMvpView> extends MvpPresenter<V> {
    void checkEmail(String email);
    void externalAuth(ExternalAuthorize authorize);
}
