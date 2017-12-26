package com.ros.smartrocket.presentation.question.video;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.utils.DialogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class VideoQuestionView extends BaseQuestionView<VideoMvpPresenter<VideoMvpView>> implements VideoMvpView, MediaPlayer.OnCompletionListener {
    public static final int DELAY_MILLIS = 700;
    @BindView(R.id.videoQuestion)
    VideoView videoView;
    @BindView(R.id.reVideoButton)
    ImageButton reVideoButton;
    @BindView(R.id.confirmButton)
    ImageButton confirmButton;
    private int stopPosition = 0;

    public VideoQuestionView(Context context) {
        super(context);
    }

    public VideoQuestionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoQuestionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_question_video;
    }

    @Override
    public void configureView(Question question) {
        videoView.setOnCompletionListener(this);
        videoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                onVideoViewClicked();
            return false;
        });
        presenter.loadAnswers();
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        presenter.refreshNextButton();
    }

    @OnClick({R.id.reVideoButton, R.id.confirmButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reVideoButton:
                presenter.onVideoRequested();
                break;
            case R.id.confirmButton:
                presenter.onVideoConfirmed();
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPosition = 0;
    }

    @Override
    public void refreshConfirmButton(boolean isVideoConfirmed, boolean isVideoAdded) {
        if (isVideoAdded) {
            confirmButton.setVisibility(View.VISIBLE);
            if (isVideoConfirmed) {
                confirmButton.setBackgroundResource(R.drawable.btn_square_green);
                confirmButton.setImageResource(R.drawable.check_square_white);
            } else {
                confirmButton.setBackgroundResource(R.drawable.btn_square_active);
                confirmButton.setImageResource(R.drawable.check_square_green);
            }
        } else {
            confirmButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshReVideoButton(boolean isVideoAdded) {
        reVideoButton.setVisibility(isVideoAdded ? VISIBLE : GONE);
    }

    @Override
    public void playPauseVideo(String videoPath) {
        showLoading(false);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(mp -> {
            videoView.start();
            new Handler().postDelayed(() -> {
                hideLoading();
                videoView.setBackgroundColor(Color.TRANSPARENT);
                videoView.pause();
            }, DELAY_MILLIS);
        });
    }

    private void playVideo() {
        videoView.seekTo(stopPosition);
        videoView.start();
        videoView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void pauseVideo() {
        stopPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    public void showEmptyAnswer() {
        videoView.setVisibility(View.VISIBLE);
        videoView.setBackgroundResource(R.drawable.camera_video_icon);
    }

    @Override
    public void showBigFileToUploadDialog() {
        DialogUtils.showBigFileToUploadDialog(getContext());
    }

    @Override
    public void showPhotoCanNotBeAddDialog() {
        DialogUtils.showPhotoCanNotBeAddDialog(getContext());
    }

    private void onVideoViewClicked() {
        if (presenter.isVideoAdded()) {
            if (videoView.isPlaying())
                pauseVideo();
            else
                playVideo();
        } else {
            presenter.onVideoRequested();
        }
    }
}
