package com.ros.smartrocket.presentation.question.audio;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.QuestionAudioPlayer;
import com.ros.smartrocket.interfaces.QuestionAudioRecorder;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.ui.views.AudioControlsView;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.audio.MatrixAudioPlayer;
import com.ros.smartrocket.utils.audio.MatrixAudioRecorder;
import com.shuyu.waveview.AudioWaveView;

import java.util.List;

import butterknife.BindView;

public class AudioView extends BaseQuestionView<AudioMvpPresenter<AudioMvpView>>
        implements AudioMvpView, MatrixAudioRecorder.AudioRecordHandler, MatrixAudioPlayer.AudioPlayCallback,
        View.OnClickListener {
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

    public AudioView(Context context) {
        super(context);
    }

    public AudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void configureView(Question question) {
        audioRecorder = new MatrixAudioRecorder(audioWave, this);
        audioPlayer = new MatrixAudioPlayer(audioWave, this);
        audioControlsView.setOnControlsClickListener(this);
        presenter.loadAnswers();
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        if (answers.isEmpty())
            presenter.addEmptyAnswer();
        questionLayout.setVisibility(View.VISIBLE);
        if (presenter.isAudioAdded())
            audioControlsView.resolveDefaultPlayingUI();
        else
            audioControlsView.resolveDefaultRecordUI();
        audioWave.setVisibility(View.VISIBLE);
        updateFilePath();
        presenter.refreshNextButton();
    }

    private void updateFilePath() {
        audioPlayer.setFilePath(presenter.getFilePath());
        audioRecorder.setFilePath(presenter.getFilePath());
    }

    private void stopRecord() {
        audioWave.stopView();
        audioRecorder.stopRecording();
        audioControlsView.resolveDefaultPlayingUI();
        presenter.saveAnswerWithLocation();
    }

    @Override
    public void onRecordError() {
        presenter.setAudioAdded(false);
        audioControlsView.resolveDefaultRecordUI();
        reset();
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
        // TODO do something
        if (activity != null)
            activity.runOnUiThread(() -> chronometer.setText(progress));
    }

    @Override
    public void onPlayError() {
        presenter.setAudioAdded(false);
        reset();
    }

    @Override
    public void onPlayStopped() {
        if (audioWave != null) audioWave.stopView();
        audioControlsView.resolveDefaultPlayingUI();
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
            DialogUtils.showEndAudioRecordingDialog(App.getInstance(), new DefaultInfoDialog.DialogButtonClickListener() {
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
        DialogUtils.showDeleteAudioRecordingDialog(App.getInstance(), new DefaultInfoDialog.DialogButtonClickListener() {
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
        presenter.deleteAnswer();
        reset();
    }

    @Override
    public void showBigFileToUploadDialog() {
        DialogUtils.showBigFileToUploadDialog(getContext());
    }

    @Override
    public void reset() {
        audioWave.stopView();
        if (audioPlayer != null) {
            audioPlayer.reset();
        }
        if (audioRecorder != null) {
            audioRecorder.reset();
        }
        chronometer.setText(R.string.def_timer);
        presenter.generateAudioFilePath();
        updateFilePath();
        audioControlsView.resolveDefaultRecordUI();
        presenter.refreshNextButton();
    }


    @Override
    public int getLayoutResId() {
        return R.layout.view_audio_question;
    }

    // TODO implement in base view ovveride here
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
