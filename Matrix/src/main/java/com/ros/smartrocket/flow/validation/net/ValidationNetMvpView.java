package com.ros.smartrocket.flow.validation.net;

import com.ros.smartrocket.flow.base.NetworkMvpView;

public interface ValidationNetMvpView extends NetworkMvpView {

    void onNewTokenRetrieved();

    void onTaskStarted();

    void onAnswersSent();

    void onAnswersNotSent();

    void taskOnValidation();
}
