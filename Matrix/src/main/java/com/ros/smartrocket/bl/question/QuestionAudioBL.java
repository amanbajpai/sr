package com.ros.smartrocket.bl.question;

import android.app.Dialog;
import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.dialog.DefaultInfoDialog;
import com.ros.smartrocket.interfaces.QuestionAudioPlayer;
import com.ros.smartrocket.interfaces.QuestionAudioRecorder;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.StorageManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.audio.MatrixAudioPlayer;
import com.ros.smartrocket.utils.audio.MatrixAudioRecorder;
import com.ros.smartrocket.views.AudioControlsView;
import com.shuyu.waveview.AudioWaveView;
import com.shuyu.waveview.FileUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionAudioBL extends QuestionBaseBL implements View.OnClickListener, MatrixAudioRecorder.RecordErrorHandler, MatrixAudioPlayer.AudioPlayCallback {
    @BindView(R.id.audioView)
    AudioControlsView audioControlsView;
    @BindView(R.id.recordAudioWave)
    AudioWaveView audioWave;
    @BindView(R.id.audioQuestionLayout)
    LinearLayout questionLayout;
    private QuestionAudioRecorder audioRecorder;
    private QuestionAudioPlayer audioPlayer;
    private boolean isAudioAdded = false;
    private String audioPath;
    private static final Random RANDOM = new Random();

    @Override
    public void configureView() {
        ButterKnife.bind(view);
        audioRecorder = new MatrixAudioRecorder(audioWave, this);
        audioPlayer = new MatrixAudioPlayer(audioWave, this);
        audioControlsView.setOnControlsClickListener(this);
        loadAnswers();
    }

    @Override
    public void fillViewWithAnswers(Answer[] answers) {
        if (answers.length == 0) {
            question.setAnswers(addEmptyAnswer(answers));
        } else {
            question.setAnswers(answers);
        }
        questionLayout.setVisibility(View.VISIBLE);
        Answer answer = question.getAnswers()[0];
        if (answer.getChecked() && answer.getFileUri() != null) {
            isAudioAdded = true;
            audioPath = Uri.parse(answer.getFileUri()).getPath();
            audioControlsView.resolveDefaultPlayingUI();
        } else {
            isAudioAdded = false;
            audioPath = generateAudioFilePath();
            audioControlsView.resolveDefaultRecordUI();
        }
        audioWave.setVisibility(View.VISIBLE);
        updateFilePath();
        refreshNextButton();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecordPause:
                handleRecordClick();
                break;
            case R.id.btnStop:
                handleStopRecordClick();
                break;
            case R.id.btnPlayStop:
                handlePlayStopClick();
                break;
            case R.id.btnTrash:
                handleDeleteClick();
                break;
        }
    }

    private void handleRecordClick() {
        if (audioRecorder.isRecording()) {
            audioControlsView.resolvePauseRecordUI();
            audioRecorder.pauseRecording();
        } else {
            audioRecorder.resumeRecording();
            audioControlsView.resolveStartRecordUI();
        }
    }

    private void handleStopRecordClick() {
        audioRecorder.pauseRecording();
        DialogUtils.showEndAudioRecordingDialog(getActivity(), new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
                audioRecorder.resumeRecording();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                stopRecord();
            }
        });
    }

    private void handlePlayStopClick() {
        if (audioPlayer.isPlaying()) {
            pausePlayer();
        } else {
            resumePlayer();
        }
    }

    private void resumePlayer() {
        audioPlayer.play();
        audioControlsView.resolveStartPlayingUI();
    }

    private void pausePlayer() {
        audioPlayer.pause();
        audioControlsView.resolveStopPlayingUI();
    }

    private void handleDeleteClick() {
        pausePlayer();
        DialogUtils.showDeleteAudioRecordingDialog(getActivity(), new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                deleteRecord();
            }
        });
    }

    private void deleteRecord() {
        if (isAudioAdded) {
            AnswersBL.deleteAnswerFromDB(handler, question.getAnswers()[0]);
        }
        isAudioAdded = false;
        FileUtils.deleteFile(audioPath);
        reset();
    }

    @Override
    public void onRecordError() {
        isAudioAdded = false;
        audioControlsView.resolveDefaultRecordUI();
        reset();
    }

    @Override
    public void onPlayError() {
        isAudioAdded = false;
        reset();
    }

    @Override
    public void onPlayStopped() {
        audioWave.stopView();
        audioControlsView.resolveDefaultPlayingUI();
    }

    private void reset() {
        if (audioPlayer != null) {
            audioPlayer.reset();
        }
        if (audioRecorder != null) {
            audioRecorder.reset();
        }
        audioPath = generateAudioFilePath();
        updateFilePath();
        audioControlsView.resolveDefaultRecordUI();
        refreshNextButton();
    }


    private String generateAudioFilePath() {
        return StorageManager.getAudioCacheDirPath(App.getInstance()) + "/" + question.getTaskId().toString() + "_" + Calendar.getInstance().getTimeInMillis() + "_"
                + RANDOM.nextInt(Integer.MAX_VALUE) + ".mp3";
    }

    private void updateFilePath() {
        audioPlayer.setFilePath(audioPath);
        audioRecorder.setFilePath(audioPath);
    }

    @Override
    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = isAudioAdded;
            answerSelectedListener.onAnswerSelected(selected, question.getId());
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    private void stopRecord() {
        audioRecorder.stopRecording();
        audioControlsView.resolveDefaultPlayingUI();
        MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager
                .GetCurrentLocationListener() {
            @Override
            public void getLocationStart() {
                showProgressDialog();
            }

            @Override
            public void getLocationInProcess() {
            }

            @Override
            public void getLocationSuccess(Location location) {
                if (getActivity() == null) {
                    return;
                }
                hideProgressDialog();
                saveAnswer(location);
            }

            @Override
            public void getLocationFail(String errorText) {
                if (!getActivity().isFinishing()) {
                    UIUtils.showSimpleToast(getActivity(), errorText);
                }
            }
        });
    }

    private void saveAnswer(Location location) {
        File sourceAudioFile = new File(audioPath);

        if (sourceAudioFile.exists()) {
            if (sourceAudioFile.length() > getActivity().getResources().getInteger(R.integer.max_video_file_size_byte)) {
                DialogUtils.showBigFileToUploadDialog(getActivity());
            }
            Answer answer = question.getAnswers()[0];
            answer.setChecked(true);
            answer.setFileUri(audioPath);
            answer.setFileSizeB(sourceAudioFile.length());
            answer.setFileName(sourceAudioFile.getName());
            answer.setValue(sourceAudioFile.getName());
            answer.setLatitude(location.getLatitude());
            answer.setLongitude(location.getLongitude());
            if (!isPreview()) {
                AnswersBL.updateAnswersToDB(handler, question.getAnswers());
            }
            isAudioAdded = true;
            refreshNextButton();
        } else {
            reset();
        }
    }


    @Override
    protected void answersDeleteComplete() {
        if (getProductId() != null) {
            AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId(),
                    getProductId());
        } else {
            AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideProgressDialog();
    }

    @Override
    public void destroyView() {
        super.destroyView();
        if (audioRecorder != null) {
            audioRecorder.reset();
        }
        if (audioPlayer != null) {
            audioPlayer.reset();
        }
    }


}
