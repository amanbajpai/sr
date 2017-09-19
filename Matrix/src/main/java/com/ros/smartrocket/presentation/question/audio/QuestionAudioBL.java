package com.ros.smartrocket.presentation.question.audio;

import android.app.Dialog;
import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.interfaces.QuestionAudioPlayer;
import com.ros.smartrocket.interfaces.QuestionAudioRecorder;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.presentation.question.base.QuestionBaseBL;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.ui.views.AudioControlsView;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.StorageManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.audio.MatrixAudioPlayer;
import com.ros.smartrocket.utils.audio.MatrixAudioRecorder;
import com.shuyu.waveview.AudioWaveView;
import com.shuyu.waveview.FileUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionAudioBL extends QuestionBaseBL implements View.OnClickListener, MatrixAudioRecorder.AudioRecordHandler, MatrixAudioPlayer.AudioPlayCallback {
    @BindView(R.id.audioView)
    AudioControlsView audioControlsView;
    @BindView(R.id.recordAudioWave)
    AudioWaveView audioWave;
    @BindView(R.id.audioQuestionLayout)
    LinearLayout questionLayout;
    @BindView(R.id.chronometer)
    CustomTextView chronometer;
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
        if (audioPlayer != null && audioRecorder != null) {
            switch (v.getId()) {
                case R.id.btnRecordPause:
                    handleRecordClick();
                    break;
                case R.id.btnStop:
                    handleStopRecordClick();
                    break;
                case R.id.btnPlayPause:
                    handlePlayPauseClick();
                    break;
                case R.id.btnPlayStop:
                    handlePlayerStopClick();
                    break;
                case R.id.btnTrash:
                    handleDeleteClick();
                    break;
            }
        }
    }

    private void handleRecordClick() {
        if (audioRecorder != null) {
            if (audioRecorder.isRecording()) {
                audioControlsView.resolvePauseRecordUI();
                audioRecorder.pauseRecording();
            } else {
                audioRecorder.resumeRecording();
                audioControlsView.resolveStartRecordUI();
            }
        }
    }

    private void handleStopRecordClick() {
        if (audioRecorder != null) {
            audioControlsView.resolvePauseRecordUI();
            audioRecorder.pauseRecording();
            DialogUtils.showEndAudioRecordingDialog(getActivity(), new DefaultInfoDialog.DialogButtonClickListener() {
                @Override
                public void onLeftButtonPressed(Dialog dialog) {
                    dialog.dismiss();
                }

                @Override
                public void onRightButtonPressed(Dialog dialog) {
                    dialog.dismiss();
                    stopRecord();
                }
            });
        }
    }

    private void handlePlayPauseClick() {
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                pausePlayer();
            } else {
                resumePlayer();
            }
        }
    }

    private void handlePlayerStopClick() {
        if (audioPlayer != null) {
            audioPlayer.pause();
            audioPlayer.stop();
            audioControlsView.resolveDefaultPlayingUI();
        }
    }

    private void resumePlayer() {
        if (audioPlayer != null) {
            audioPlayer.play();
            audioControlsView.resolveStartPlayingUI();
        }
    }

    private void pausePlayer() {
        if (audioPlayer != null) {
            audioPlayer.pause();
            audioControlsView.resolvePausePlayingUI();
        }
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
        if (audioWave != null) {
            audioWave.stopView();
        }
        audioControlsView.resolveDefaultPlayingUI();
    }

    @Override
    public void onRecordProgress(String progress) {
        updateTimer(progress);
    }

    @Override
    public void onPlayProgress(String progress) {
        updateTimer(progress);
    }

    private void updateTimer(final String progress) {
        if (activity != null && fragment != null && fragment.isAdded()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chronometer.setText(progress);
                }
            });
        }
    }

    private void reset() {
        audioWave.stopView();
        if (audioPlayer != null) {
            audioPlayer.reset();
        }
        if (audioRecorder != null) {
            audioRecorder.reset();
        }
        chronometer.setText(R.string.def_timer);
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
        audioWave.stopView();
        audioRecorder.stopRecording();
        audioControlsView.resolveDefaultPlayingUI();
        MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager
                .GetCurrentLocationListener() {
            @Override
            public void getLocationStart() {
                showLoading();
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
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                pausePlayer();
            } else {
                audioPlayer.stop();
            }
        }
        if (audioWave != null) {
            audioWave.stopView();
        }
    }

    @Override
    public void destroyView() {
        clearResources();
        super.destroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (audioWave != null && audioWave.getVisibility() == View.VISIBLE) {
            audioWave.setVisibility(View.VISIBLE);
            audioWave.startView();
        }
    }

    private void clearResources() {
        audioWave.stopView();
        if (audioRecorder != null) {
            audioRecorder.reset();
            audioRecorder = null;
        }
        if (audioPlayer != null) {
            audioPlayer.pause();
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer = null;
        }
        audioWave = null;
    }


}
