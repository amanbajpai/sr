package com.ros.smartrocket.presentation.question.audio;

import android.location.Location;
import android.net.Uri;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.StorageManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.audio.SelectAudioManager;
import com.shuyu.waveview.FileUtils;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AudioPresenter<V extends AudioMvpView> extends BaseQuestionPresenter<V> implements AudioMvpPresenter<V> {
    private Random RANDOM = new Random();
    private String filePath;
    private boolean isAudioAdded;

    public AudioPresenter(Question question) {
        super(question);
    }

    @Override
    public void onAnswersLoadedFromDb(List<Answer> answers) {
        if (answers.isEmpty()) addEmptyAnswer();
        Answer answer = answers.get(0);
        if (answer.getChecked() && answer.getFileUri() != null) {
            isAudioAdded = true;
            filePath = Uri.parse(answer.getFileUri()).getPath();
        } else {
            isAudioAdded = false;
            generateAudioFilePath();
        }
        super.onAnswersLoadedFromDb(answers);
    }

    @Override
    public void onAnswersDeleted() {
        loadAnswers();
    }

    @Override
    public void generateAudioFilePath() {
        filePath = StorageManager.getAudioCacheDirPath(App.getInstance()) + "/" + question.getTaskId().toString() + "_" + Calendar.getInstance().getTimeInMillis() + "_"
                + RANDOM.nextInt(Integer.MAX_VALUE) + ".mp3";
    }

    @Override
    public void saveAnswerWithLocation() {
        MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager
                .GetCurrentLocationListener() {
            @Override
            public void getLocationStart() {
                getMvpView().showLoading(false);
            }

            @Override
            public void getLocationInProcess() {
            }

            @Override
            public void getLocationSuccess(Location location) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    saveAnswer(location);
                }
            }

            @Override
            public void getLocationFail(String errorText) {
                if (isViewAttached())
                    UIUtils.showSimpleToast(App.getInstance(), errorText);
            }
        });
    }

    private void saveAnswer(Location location) {
        File sourceAudioFile = new File(filePath);
        if (sourceAudioFile.exists()) {
            // Store Audio to Media folder here
            if (PreferencesManager.getInstance().getUseSaveMediaToDevice()) {
                File lastFile = SelectAudioManager.copyFileToTempFolder(App.getInstance(), new File(filePath), sourceAudioFile.getName());
            }

            if (sourceAudioFile.length() > Keys.MAX_VIDEO_FILE_SIZE_BYTE) {
                getMvpView().showBigFileToUploadDialog();
                getMvpView().reset();
                return;
            }

            Answer answer = question.getAnswers().get(0);
            answer.setChecked(true);
            answer.setFileUri(filePath);
            answer.setFileSizeB(sourceAudioFile.length());
            answer.setFileName(sourceAudioFile.getName());
            answer.setValue(sourceAudioFile.getName());
            answer.setLatitude(location.getLatitude());
            answer.setLongitude(location.getLongitude());
            if (!isPreview()) saveQuestion();
            isAudioAdded = true;
            refreshNextButton();
        } else {
            getMvpView().reset();
        }
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean isAudioAdded() {
        return isAudioAdded;
    }

    @Override
    public void setAudioAdded(boolean isAudioAdded) {
        this.isAudioAdded = isAudioAdded;
    }

    @Override
    public void refreshNextButton() {
        refreshNextButton(isAudioAdded);
    }

    @Override
    public void deleteAnswer() {
        isAudioAdded = false;
        FileUtils.deleteFile(filePath);
        if (isAudioAdded) deleteAnswer(question.getAnswers().get(0));
    }
}
