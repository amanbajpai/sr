package com.ros.smartrocket.presentation.question.audio;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpView;

public interface AudioMvpView extends BaseQuestionMvpView {
    void showBigFileToUploadDialog();
    void reset();

}
