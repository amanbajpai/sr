package com.ros.smartrocket.flow.login.terms;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface TermsMvpPresenter<V extends TermsMvpView> extends MvpPresenter<V> {

    void sendTermsAndConditionsViewed();
}
