package com.ros.smartrocket.flow.share;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface ShareMvpPresenter<V extends ShareMvpView> extends MvpPresenter<V> {
    void getSharingData();
}
