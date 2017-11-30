package com.ros.smartrocket.presentation.question.video;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

public interface VideoMvpPresenter<V extends VideoMvpView> extends BaseQuestionMvpPresenter<V> {

    void refreshNextButton();

    void onVideoRequested();

    void onVideoConfirmed();

    boolean isVideoAdded();
}
