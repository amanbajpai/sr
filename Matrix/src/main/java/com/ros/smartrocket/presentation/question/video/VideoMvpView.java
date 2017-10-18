package com.ros.smartrocket.presentation.question.video;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpView;

public interface VideoMvpView extends BaseQuestionMvpView {

    void refreshConfirmButton(boolean isVideoConfirmed, boolean isVideoAdded);

    void refreshReVideoButton(boolean isVideoAdded);

    void playPauseVideo(String videoPath);

    void showEmptyAnswer();

    void showBigFileToUploadDialog();

    void showPhotoCanNotBeAddDialog();
}
