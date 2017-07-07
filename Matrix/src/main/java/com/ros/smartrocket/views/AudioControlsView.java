package com.ros.smartrocket.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.helpers.AVDWrapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioControlsView extends LinearLayout implements AVDWrapper.AnimationCallback {
    private static final int ANIMATION_DURATION = 470;
    @Bind(R.id.btnRecordPause)
    AppCompatImageView btnRecord;
    @Bind(R.id.btnStop)
    AppCompatImageView btnStopRecord;
    @Bind(R.id.btnPlayStop)
    AppCompatImageView btnPlay;
    @Bind(R.id.btnTrash)
    AppCompatImageView btnDelete;
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
        inflate(c, R.layout.view_question_audio, this);
        ButterKnife.bind(this);
        animationWrapper = new AVDWrapper();
        animationWrapper.setCallback(this);
    }

    @Override
    public void onAnimationDone(AppCompatImageView imageView) {
        if (onClickListener != null) {
            onClickListener.onClick(imageView);
        }
    }

    @OnClick({R.id.btnRecordPause, R.id.btnStop, R.id.btnPlayStop, R.id.btnTrash})
    public void onViewClicked(View view) {
        if (animationWrapper != null && view instanceof AppCompatImageView) {
            animationWrapper.start(ANIMATION_DURATION, (AppCompatImageView) view);
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
        btnPlay.setVisibility(VISIBLE);
        btnDelete.setVisibility(VISIBLE);
        btnRecord.setVisibility(GONE);
        btnStopRecord.setVisibility(GONE);
    }

    public void resolveStartPlayingUI() {
        btnPlay.setImageResource(R.drawable.avd_pause);
    }

    public void resolveStopPlayingUI() {
        btnPlay.setImageResource(R.drawable.avd_play);
    }

    public void resolveDefaultRecordUI() {
        btnRecord.setImageResource(R.drawable.avd_record);
        btnRecord.setVisibility(VISIBLE);
        btnStopRecord.setVisibility(GONE);
        btnPlay.setVisibility(GONE);
        btnDelete.setVisibility(GONE);
    }

}
