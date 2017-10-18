package com.ros.smartrocket.presentation.question.video;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;
import com.ros.smartrocket.presentation.question.video.helper.VideoHelper;
import com.ros.smartrocket.utils.SelectVideoManager;
import com.ros.smartrocket.utils.UIUtils;

import java.io.File;
import java.util.List;

public class VideoPresenter<V extends VideoMvpView> extends BaseQuestionPresenter<V> implements VideoMvpPresenter<V> {
    private String videoPath;
    private boolean isVideoAdded = false;
    private boolean isVideoConfirmed = false;
    private boolean isVideoRequested;
    private VideoHelper videoHelper;

    public VideoPresenter(Question question, VideoHelper videoHelper) {
        super(question);
        this.videoHelper = videoHelper;
    }

    @Override
    public void onAnswersLoadedFromDb(List<Answer> answers) {
        if (answers.isEmpty()) addEmptyAnswer();
        Answer answer = question.getAnswers().get(0);
        if (answer.getChecked() && answer.getFileUri() != null) {
            isVideoAdded = true;
            isVideoConfirmed = true;
            videoPath = Uri.parse(answer.getFileUri()).getPath();
            getMvpView().playPauseVideo(videoPath);
        } else {
            isVideoAdded = false;
            isVideoConfirmed = false;
            getMvpView().showEmptyAnswer();
        }
        super.onAnswersLoadedFromDb(answers);
        refreshButtons();
    }

    private void refreshButtons() {
        getMvpView().refreshReVideoButton(isVideoAdded);
        getMvpView().refreshConfirmButton(isVideoConfirmed, isVideoAdded);
    }

    @Override
    public void refreshNextButton() {
        refreshNextButton(!TextUtils.isEmpty(question.getAnswers().get(0).getFileUri()));
    }

    @Override
    public void onVideoRequested() {
        if (question.getVideoSource() == 0)
            videoHelper.startCamera();
        else if (question.getVideoSource() == 1)
            videoHelper.startGallery();
        else
            videoHelper.showSelectVideoDialog();
        isVideoRequested = true;
        listenForVideo();
    }

    @Override
    public void onVideoConfirmed() {
        if (!isVideoConfirmed) {
            MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager
                    .GetCurrentLocationListener() {
                @Override
                public void getLocationStart() {
                    getMvpView().showLoading(true);
                }

                @Override
                public void getLocationInProcess() {
                }

                @Override
                public void getLocationSuccess(Location location) {
                    if (isViewAttached()) {
                        saveAnswer(location);
                        getMvpView().hideLoading();
                    }
                }

                @Override
                public void getLocationFail(String errorText) {
                    if (isViewAttached()) {
                        getMvpView().hideLoading();
                        UIUtils.showSimpleToast(App.getInstance(), errorText);
                    }
                }
            });
        }
    }

    private void saveAnswer(Location location) {
        File sourceVideoFile = new File(videoPath);
        if (sourceVideoFile.exists()) {
            Answer answer = question.getAnswers().get(0);
            answer.setChecked(true);
            answer.setFileUri(videoPath);
            answer.setFileSizeB(sourceVideoFile.length());
            answer.setFileName(sourceVideoFile.getName());
            answer.setValue(sourceVideoFile.getName());
            answer.setLatitude(location.getLatitude());
            answer.setLongitude(location.getLongitude());
            if (!isPreview()) saveQuestion();
            isVideoConfirmed = true;
            refreshButtons();
            refreshNextButton();
        }
    }

    private void listenForVideo() {
        videoHelper.setVideoCompleteListener(new SelectVideoManager.OnVideoCompleteListener() {
            @Override
            public void onVideoComplete(String videoFilePath) {
                if (isViewAttached()) {
                    File sourceImageFile = new File(videoFilePath);
                    if (sourceImageFile.length() > Keys.MAX_VIDEO_FILE_SIZE_BYTE) {
                        getMvpView().showBigFileToUploadDialog();
                    } else {
                        videoPath = videoFilePath;
                        isVideoAdded = !TextUtils.isEmpty(videoPath);
                        isVideoConfirmed = false;
                        answerSelectedListener.onAnswerSelected(false, question.getId());
                        if (!TextUtils.isEmpty(videoPath))
                            getMvpView().playPauseVideo(videoPath);
                        else
                            getMvpView().showEmptyAnswer();
                        refreshButtons();
                    }
                }
            }

            @Override
            public void onSelectVideoError(int imageFrom) {
                if (isViewAttached())
                    getMvpView().showPhotoCanNotBeAddDialog();
            }
        });
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (isVideoRequested)
            videoHelper.onActivityResult(requestCode, resultCode, intent);
        return true;
    }

    @Override
    public void onAnswersUpdated() {
        if (isViewAttached())
            getMvpView().hideLoading();
    }
}
