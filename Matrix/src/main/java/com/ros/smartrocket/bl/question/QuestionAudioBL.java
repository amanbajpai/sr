package com.ros.smartrocket.bl.question;

import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.helpers.AVDWrapper;
import com.shuyu.waveview.AudioWaveView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionAudioBL extends QuestionBaseBL implements AVDWrapper.AnimationCallback {
    private static final int ANIMATION_DURATION = 470;
    @Bind(R.id.btnRecord)
    AppCompatImageView btnRecord;
    @Bind(R.id.btnPause)
    AppCompatImageView btnPause;
    @Bind(R.id.btnStop)
    AppCompatImageView btnStop;
    @Bind(R.id.btnPlay)
    AppCompatImageView btnPlay;
    @Bind(R.id.btnTrash)
    AppCompatImageView btnTrash;
    @Bind(R.id.audioWave)
    AudioWaveView audioWave;
    private AVDWrapper animationWrapper;

    @Override
    public void configureView() {
        ButterKnife.bind(view);
        animationWrapper = new AVDWrapper();
        animationWrapper.setCallback(this);
    }

    @Override
    public void fillViewWithAnswers(Answer[] answers) {
    }

    private void handleDeleteClick() {
        btnRecord.setVisibility(View.VISIBLE);
        btnTrash.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
        btnPause.setVisibility(View.GONE);
    }

    private void handlePlayClick() {
        btnPause.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.GONE);
    }

    private void handleStopClick() {
        btnPlay.setVisibility(View.VISIBLE);
        btnTrash.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
        btnRecord.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);
    }

    private void handlePauseClick() {
        btnPause.setVisibility(View.GONE);
        btnRecord.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.VISIBLE);
    }

    private void handleRecordClick() {
        btnRecord.setVisibility(View.GONE);
        btnPause.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.btnRecord, R.id.btnPause, R.id.btnStop, R.id.btnPlay, R.id.btnTrash, R.id.audioWave})
    public void onViewClicked(View view) {
        if (animationWrapper != null && view instanceof AppCompatImageView) {
            animationWrapper.start(ANIMATION_DURATION, (AppCompatImageView) view);
        }
    }

    @Override
    public void onAnimationDone(AppCompatImageView imageView) {
        switch (imageView.getId()) {
            case R.id.btnRecord:
                handleRecordClick();
                break;
            case R.id.btnPause:
                handlePauseClick();
                break;
            case R.id.btnStop:
                handleStopClick();
                break;
            case R.id.btnPlay:
                handlePlayClick();
                break;
            case R.id.btnTrash:
                handleDeleteClick();
                break;
        }
    }
}
