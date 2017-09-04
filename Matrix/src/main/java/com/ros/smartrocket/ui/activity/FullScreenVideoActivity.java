package com.ros.smartrocket.ui.activity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.VideoView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenVideoActivity extends BaseActivity implements MediaPlayer.OnCompletionListener {
    @BindView(R.id.video)
    VideoView videoView;
    private int stopPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUp();
        setContentView(R.layout.activity_full_screen_video);
        ButterKnife.bind(this);

        String videoPath = getIntent().getStringExtra(Keys.VIDEO_FILE_PATH);
        if (videoPath != null) {
            videoView.setVideoPath(videoPath);
        } else {
            UIUtils.showSimpleToast(this, R.string.error);
        }
        videoView.setOnCompletionListener(this);
        videoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (videoView.isPlaying()) {
                    pauseVideo();
                } else {
                    playVideo();
                }
            }

            return false;
        });

        videoView.setOnPreparedListener(mp -> {
            videoView.setBackgroundColor(Color.TRANSPARENT);
            videoView.start();
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPosition = 0;
    }

    public void playVideo() {
        videoView.seekTo(stopPosition);
        videoView.start();
        videoView.setBackgroundColor(Color.TRANSPARENT);
    }

    public void pauseVideo() {
        stopPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
