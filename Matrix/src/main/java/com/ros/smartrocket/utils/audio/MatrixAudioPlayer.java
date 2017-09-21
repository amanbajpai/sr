package com.ros.smartrocket.utils.audio;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.piterwilson.audio.MP3RadioStreamDelegate;
import com.piterwilson.audio.MP3RadioStreamPlayer;
import com.ros.smartrocket.interfaces.QuestionAudioPlayer;
import com.ros.smartrocket.utils.TimeUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.shuyu.waveview.AudioWaveView;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MatrixAudioPlayer implements QuestionAudioPlayer, MP3RadioStreamDelegate {
    private MP3RadioStreamPlayer audioPlayer;
    private String filePath;
    private AudioPlayCallback playerHandler;
    private AudioWaveView audioWave;
    private boolean isPlayEnded = false;
    private boolean isPlaying = false;
    private Handler handler;
    private Timer timer;
    private long duration;

    public MatrixAudioPlayer(AudioWaveView audioWave, AudioPlayCallback playerHandler) {
        this.playerHandler = playerHandler;
        this.audioWave = audioWave;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void play() {
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
            onPlayError();
            return;
        }
        if (audioPlayer != null && !isPlayEnded && audioPlayer.isPause()) {
            resume();
        } else {
            duration = 0;
            isPlayEnded = false;
            audioPlayer = new MP3RadioStreamPlayer();
            audioPlayer.setUrlString(filePath);
            audioPlayer.setDelegate(this);
            audioPlayer.setDataList(audioWave.getRecList(), UIUtils.getMaxAudioWaveSize());
            audioWave.startView();
            try {
                audioPlayer.play();
                isPlaying = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer = null;
            updateProgress(0);
        }
    }

    private void scheduleTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isPlaying || audioPlayer == null || isPlayEnded) {
                    return;
                }
                if (playerHandler != null) {
                    updateProgress(audioPlayer.getCurPosition());
                }
            }
        }, TimeUtils.ONE_SECOND_IN_MILL, TimeUtils.ONE_SECOND_IN_MILL);
    }

    private void updateProgress(long currPos) {
        playerHandler.onPlayProgress(TimeUtils.toTime(currPos / TimeUtils.ONE_SECOND_IN_MILL)
                + " / " + TimeUtils.toTime(duration));
    }

    @Override
    public void pause() {
        if (audioPlayer != null) {
            audioPlayer.setPause(true);
        }
        isPlayEnded = false;
        isPlaying = false;
    }

    @Override
    public void reset() {
        duration = 0;
        updateProgress(0);
        isPlayEnded = false;
        audioPlayer = null;
        isPlaying = false;
    }

    private void resume() {
        if (audioPlayer != null) {
            audioPlayer.setPause(false);
        }
        isPlayEnded = false;
        isPlaying = true;
    }

    @Override
    public void setFilePath(String path) {
        filePath = path;
    }

    private void onPlayError() {
        if (playerHandler != null) {
            playerHandler.onPlayError();
            isPlaying = false;
        }
    }

    private void onPlayStopped() {
        if (playerHandler != null) {
            playerHandler.onPlayStopped();
        }
        cancelTimer();
        updateProgress(0);
    }

    @Override
    public void onRadioPlayerPlaybackStarted(MP3RadioStreamPlayer mp3RadioStreamPlayer) {
        isPlaying = true;
        isPlayEnded = false;
        duration = mp3RadioStreamPlayer.getDuration() / TimeUtils.ONE_SECOND_IN_MILL;
        updateProgress(mp3RadioStreamPlayer.getCurPosition());
        scheduleTimer();
    }

    @Override
    public void onRadioPlayerStopped(MP3RadioStreamPlayer mp3RadioStreamPlayer) {
        isPlayEnded = true;
        isPlaying = false;
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPlayStopped();
            }
        });
    }

    @Override
    public void onRadioPlayerError(MP3RadioStreamPlayer mp3RadioStreamPlayer) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPlayError();
            }
        });
    }

    @Override
    public void onRadioPlayerBuffering(MP3RadioStreamPlayer mp3RadioStreamPlayer) {
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public interface AudioPlayCallback {
        void onPlayError();

        void onPlayStopped();

        void onPlayProgress(String progress);
    }
}

