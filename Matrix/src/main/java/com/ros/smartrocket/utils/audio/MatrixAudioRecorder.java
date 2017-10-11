package com.ros.smartrocket.utils.audio;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.czt.mp3recorder.MP3Recorder;
import com.ros.smartrocket.interfaces.QuestionAudioRecorder;
import com.ros.smartrocket.utils.TimeUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.shuyu.waveview.AudioWaveView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MatrixAudioRecorder implements QuestionAudioRecorder {
    private AudioWaveView audioWave;
    private MP3Recorder recorder;
    private String filePath;
    private boolean isRecording;
    private AudioRecordHandler recordHandler;
    private Timer timer;
    private long time;


    public MatrixAudioRecorder(AudioWaveView audioWave, AudioRecordHandler recordHandler) {
        this.audioWave = audioWave;
        this.recordHandler = recordHandler;
    }

    @Override
    public void startRecording() {
        if (TextUtils.isEmpty(filePath)) {
            onRecordError();
            return;
        }
        time = 0;
        File file = new File(filePath);
        recorder = new MP3Recorder(file);
        recorder.setDataList(audioWave.getRecList(), UIUtils.getMaxAudioWaveSize());
        WeakReference<Handler> handler = new WeakReference<>(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MP3Recorder.ERROR_TYPE) onRecordError();
            }
        });
        recorder.setErrorHandler(handler.get());
        try {
            recorder.start();
            if (recordHandler != null) recordHandler.onRecordProgress("00:00");
            scheduleTimer();
            audioWave.startView();
        } catch (IOException e) {
            e.printStackTrace();
            onRecordError();
            return;
        }
        isRecording = true;
    }

    private void scheduleTimer() {
        timer = new Timer();
        // TODO replace with RxJava
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isRecording || recorder == null) return;
                time++;
                if (recordHandler != null)
                    recordHandler.onRecordProgress(TimeUtils.toTime(time * TimeUtils.ONE_SECOND_IN_MILL));
            }
        }, TimeUtils.ONE_SECOND_IN_MILL, TimeUtils.ONE_SECOND_IN_MILL);
    }

    @Override
    public void stopRecording() {
        if (recorder != null && recorder.isRecording()) {
            recorder.setPause(false);
            recorder.stop();
        }
        cancelTimer();
        isRecording = false;
    }

    @Override
    public void pauseRecording() {
        if (recorder != null && !recorder.isPause()) {
            recorder.setPause(true);
            isRecording = false;
        }
    }

    @Override
    public void resumeRecording() {
        if (recorder == null) {
            startRecording();
        } else if (recorder.isPause()) {
            recorder.setPause(false);
            isRecording = true;
        }
    }

    @Override
    public void reset() {
        isRecording = false;
        filePath = "";
        cancelTimer();
        if (recorder != null) {
            recorder.stop();
            recorder = null;
        }

    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void onRecordError() {
        reset();
        if (recordHandler != null) {
            recordHandler.onRecordError();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    @Override
    public void setFilePath(String path) {
        filePath = path;
    }

    public interface AudioRecordHandler {
        void onRecordError();

        void onRecordProgress(String progress);
    }
}
