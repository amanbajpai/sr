package com.ros.smartrocket.presentation.login.terms;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface TermsMvpPresenter<V extends TermsMvpView> extends MvpPresenter<V> {

    void sendTermsAndConditionsViewed();
}
