package com.ros.smartrocket.utils.audio;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.piterwilson.audio.MP3RadioStreamDelegate;
import com.piterwilson.audio.MP3RadioStreamPlayer;
import com.ros.smartrocket.interfaces.QuestionAudioPlayer;
import com.ros.smartrocket.utils.UIUtils;
import com.shuyu.waveview.AudioWaveView;

import java.io.File;
import java.io.IOException;

public class MatrixAudioPlayer implements QuestionAudioPlayer, MP3RadioStreamDelegate {
    private MP3RadioStreamPlayer audioPlayer;
    private String filePath;
    private AudioPlayCallback errorHandler;
    private AudioWaveView audioWave;
    private boolean isPlayEnded = false;
    private boolean isPlaying = false;
    private Handler handler;

    public MatrixAudioPlayer(AudioWaveView audioWave, AudioPlayCallback errorHandler) {
        this.errorHandler = errorHandler;
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
    public void pause() {
        if (audioPlayer != null) {
            audioPlayer.setPause(true);
        }
        isPlaying = false;
    }

    @Override
    public void reset() {
        audioWave.stopView();
        isPlaying = false;
    }

    private void resume() {
        if (audioPlayer != null) {
            audioPlayer.setPause(false);
        }
        isPlaying = true;
    }

    @Override
    public void setFilePath(String path) {
        filePath = path;
    }

    private void onPlayError() {
        if (errorHandler != null) {
            errorHandler.onPlayError();
            isPlaying = false;
        }
    }

    private void onPlayStopped() {
        if (errorHandler != null) {
            errorHandler.onPlayStopped();
            isPlaying = false;
        }
    }

    @Override
    public void onRadioPlayerPlaybackStarted(MP3RadioStreamPlayer mp3RadioStreamPlayer) {
        isPlaying = true;
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

    public interface AudioPlayCallback {
        void onPlayError();

        void onPlayStopped();
    }
}

