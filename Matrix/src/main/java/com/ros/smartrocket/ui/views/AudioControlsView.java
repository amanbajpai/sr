package com.ros.smartrocket.ui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.helpers.AVDWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioControlsView extends LinearLayout implements AVDWrapper.AnimationCallback {
    private static final int ANIMATION_DURATION = 470;
    @BindView(R.id.btnRecordPause)
    AppCompatImageView btnRecord;
    @BindView(R.id.btnStop)
    AppCompatImageView btnStopRecord;
    @BindView(R.id.btnPlayPause)
    AppCompatImageView btnPlay;
    @BindView(R.id.btnPlayStop)
    AppCompatImageView btnPlayerStop;
    @BindView(R.id.btnTrash)
    AppCompatImageView btnDeleteRecord;
    private AVDWrapper animationWrapper;
    private OnClickListener onClickListener;

    public AudioControlsView(Context context) {
        super(context);
        initView(context);
    }

    public AudioControlsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AudioControlsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context c) {
        inflate(c, R.layout.view_audio_controls, this);
        ButterKnife.bind(this);
        animationWrapper = new AVDWrapper();
        animationWrapper.setCallback(this);
    }

    @Override
    public void onAnimationDone(AppCompatImageView imageView) {
        unblockButtons(imageView.getId());
        if (onClickListener != null) {
            onClickListener.onClick(imageView);
        }
    }

    private void unblockButtons(int clickedButtonId) {
        switch (clickedButtonId) {
            case R.id.btnRecordPause:
            case R.id.btnStop:
                btnRecord.setEnabled(true);
                btnStopRecord.setEnabled(true);
                break;
            case R.id.btnPlayPause:
            case R.id.btnTrash:
            case R.id.btnPlayStop:
                btnPlay.setEnabled(true);
                btnPlayerStop.setEnabled(true);
                btnDeleteRecord.setEnabled(true);
                break;
        }
    }

    @OnClick({R.id.btnRecordPause, R.id.btnStop, R.id.btnPlayPause, R.id.btnTrash, R.id.btnPlayStop})
    public void onViewClicked(View view) {
        blockButtons(view.getId());
        if (animationWrapper != null && view instanceof AppCompatImageView) {
            animationWrapper.start(ANIMATION_DURATION, (AppCompatImageView) view);
        }
    }

    private void blockButtons(int clickedButtonId) {
        switch (clickedButtonId) {
            case R.id.btnRecordPause:
            case R.id.btnStop:
                btnRecord.setEnabled(false);
                btnStopRecord.setEnabled(false);
                break;
            case R.id.btnPlayPause:
            case R.id.btnTrash:
            case R.id.btnPlayStop:
                btnPlay.setEnabled(false);
                btnPlayerStop.setEnabled(false);
                btnDeleteRecord.setEnabled(false);
                break;
        }
    }


    public void setOnControlsClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void resolveStartRecordUI() {
        btnRecord.setImageResource(R.drawable.avd_pause);
        btnStopRecord.setVisibility(VISIBLE);
    }

    public void resolvePauseRecordUI() {
        btnRecord.setImageResource(R.drawable.avd_record);
        btnRecord.setVisibility(VISIBLE);
    }

    public void resolveDefaultPlayingUI() {
        btnPlay.setImageResource(R.drawable.avd_play);
        btnPlayerStop.setVisibility(GONE);
        btnPlay.setVisibility(VISIBLE);
        btnDeleteRecord.setVisibility(VISIBLE);
        btnRecord.setVisibility(GONE);
        btnStopRecord.setVisibility(GONE);
    }

    public void resolveStartPlayingUI() {
        btnPlay.setImageResource(R.drawable.avd_pause);
        btnPlayerStop.setVisibility(VISIBLE);
    }

    public void resolvePausePlayingUI() {
        btnPlay.setImageResource(R.drawable.avd_play);
    }

    public void resolveDefaultRecordUI() {
        btnRecord.setImageResource(R.drawable.avd_record);
        btnRecord.setVisibility(VISIBLE);
        btnStopRecord.setVisibility(GONE);
        btnPlay.setVisibility(GONE);
        btnDeleteRecord.setVisibility(GONE);
        btnPlayerStop.setVisibility(GONE);
    }

}
