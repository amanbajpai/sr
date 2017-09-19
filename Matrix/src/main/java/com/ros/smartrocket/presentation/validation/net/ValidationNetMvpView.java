package com.ros.smartrocket.presentation.validation.net;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

public interface ValidationNetMvpView extends NetworkMvpView {

    void onNewTokenRetrieved();

    void onTaskStarted();

    void onAnswersSent();

    void onAnswersNotSent();

    void taskOnValidation();
}
