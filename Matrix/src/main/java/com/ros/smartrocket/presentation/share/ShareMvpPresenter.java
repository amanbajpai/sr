package com.ros.smartrocket.presentation.share;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface ShareMvpPresenter<V extends ShareMvpView> extends MvpPresenter<V> {
    void getSharingData();
}
