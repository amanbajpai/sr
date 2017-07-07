package com.ros.smartrocket.bl.question;

import android.net.Uri;
import android.view.View;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.interfaces.QuestionAudioPlayer;
import com.ros.smartrocket.interfaces.QuestionAudioRecorder;
import com.ros.smartrocket.utils.StorageManager;
import com.ros.smartrocket.utils.audio.MatrixAudioPlayer;
import com.ros.smartrocket.utils.audio.MatrixAudioRecorder;
import com.ros.smartrocket.views.AudioControlsView;
import com.shuyu.waveview.AudioWaveView;

import java.util.Calendar;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QuestionAudioBL extends QuestionBaseBL implements View.OnClickListener, MatrixAudioRecorder.RecordErrorHandler, MatrixAudioPlayer.AudioPlayCallback {
    @Bind(R.id.audioView)
    AudioControlsView audioControlsView;
    @Bind(R.id.audioWave)
    AudioWaveView audioWave;
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
    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = isAudioAdded;
            answerSelectedListener.onAnswerSelected(selected, question.getId());
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    @Override
    public void fillViewWithAnswers(Answer[] answers) {
        if (answers.length == 0) {
            question.setAnswers(addEmptyAnswer(answers));
        } else {
            question.setAnswers(answers);
        }

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
        updateFilePath();

    }

    private void updateFilePath() {
        audioPlayer.setFilePath(audioPath);
        audioRecorder.setFilePath(audioPath);
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
            audioControlsView.resolveStartRecordUI();
            audioRecorder.resumeRecording();
        }
    }

    private void handleStopRecordClick() {
        // TODO dialog
        audioRecorder.stopRecording();
        audioControlsView.resolveDefaultPlayingUI();
    }

    private void handlePlayStopClick() {
        if (audioPlayer.isPlaying()) {
            audioPlayer.pause();
            audioControlsView.resolveStopPlayingUI();
        } else {
            audioPlayer.play();
            audioControlsView.resolveStartPlayingUI();
        }
    }

    private void handleDeleteClick() {
        // TODO dialog
    }

    @Override
    public void onRecordError() {
        isAudioAdded = false;
        audioControlsView.resolveDefaultRecordUI();
    }

    @Override
    public void onPlayError() {
        isAudioAdded = false;
        audioControlsView.resolveDefaultRecordUI();
    }

    @Override
    public void onPlayStopped() {
        audioControlsView.resolveDefaultPlayingUI();
    }

    private String generateAudioFilePath() {
        return StorageManager.getAudioCacheDirPath(App.getInstance()) + "/" + question.getTaskId().toString() + "_" + Calendar.getInstance().getTimeInMillis() + "_"
                + RANDOM.nextInt(Integer.MAX_VALUE) + ".mp3";
    }

}
