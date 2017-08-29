package com.ros.smartrocket.ui.login.terms;

import com.ros.smartrocket.ui.base.MvpPresenter;

interface TermsMvpPresenter<V extends TermsMvpView> extends MvpPresenter<V> {

    void sendTermsAndConditionsViewed();
}
