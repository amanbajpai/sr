package com.ros.smartrocket.presentation.question.audio;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

public interface AudioMvpPresenter<V extends AudioMvpView> extends BaseQuestionMvpPresenter<V> {
    void generateAudioFilePath();

    void saveAnswerWithLocation();

    String getFilePath();

    boolean isAudioAdded();

    void setAudioAdded(boolean isAdded);

    void refreshNextButton();

    void deleteAnswer();
}
