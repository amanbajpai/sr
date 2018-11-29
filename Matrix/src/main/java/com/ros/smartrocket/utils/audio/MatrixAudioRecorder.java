package com.ros.smartrocket.utils.audio;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.czt.mp3recorder.MP3Recorder;
import com.ros.smartrocket.interfaces.QuestionAudioRecorder;
import com.ros.smartrocket.utils.TimeUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.shuyu.waveview.AudioWaveView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MatrixAudioRecorder implements QuestionAudioRecorder {
    private Disposable timerDisposable;
    private AudioWaveView audioWave;
    private MP3Recorder recorder;
    private String filePath;
    private boolean isRecording;
    private AudioRecordHandler recordHandler;
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
        if (timerDisposable != null) timerDisposable.dispose();
        timerDisposable =
                Observable
                        .interval(1, 1, TimeUnit.SECONDS, Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(__ ->
                        {
                            if (recordHandler != null && isRecording && recorder != null) {
                                time++;
                                recordHandler.onRecordProgress(TimeUtils.toTime(time * TimeUtils.ONE_SECOND_IN_MILL));
                            }
                        }, this::onTimerError);
    }

    private void onTimerError(Throwable t) {
        Log.e("MatrixAudioRecorder", "Error on MatrixAudioRecorder Timer", t);
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
        if (timerDisposable != null) {
            timerDisposable.dispose();
            timerDisposable = null;
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
