package com.ros.smartrocket.utils.audio;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.czt.mp3recorder.MP3Recorder;
import com.ros.smartrocket.interfaces.QuestionAudioRecorder;
import com.ros.smartrocket.utils.UIUtils;
import com.shuyu.waveview.AudioWaveView;
import com.shuyu.waveview.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MatrixAudioRecorder implements QuestionAudioRecorder {
    private AudioWaveView audioWave;
    private MP3Recorder recorder;
    private String filePath;
    private boolean isRecording;
    private RecordErrorHandler errorHandler;


    public MatrixAudioRecorder(AudioWaveView audioWave, RecordErrorHandler errorHandler) {
        this.audioWave = audioWave;
        this.errorHandler = errorHandler;
    }

    @Override
    public void startRecording() {
        if (TextUtils.isEmpty(filePath)) {
            onRecordError();
            return;
        }
        File file = new File(filePath);
        recorder = new MP3Recorder(file);
        recorder.setDataList(audioWave.getRecList(), UIUtils.getMaxAudioWaveSize());
        WeakReference<Handler> handler = new WeakReference<Handler>(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MP3Recorder.ERROR_TYPE) {
                    onRecordError();
                }
            }
        });
        recorder.setErrorHandler(handler.get());
        try {
            recorder.start();
            audioWave.startView();
        } catch (IOException e) {
            e.printStackTrace();
            onRecordError();
            return;
        }
        isRecording = true;
    }

    @Override
    public void stopRecording() {
        if (recorder != null && recorder.isRecording()) {
            recorder.setPause(false);
            recorder.stop();
            audioWave.stopView();
        }
        isRecording = false;
    }

    @Override
    public void pauseRecording() {
        if (!isRecording)
            return;
        if (!recorder.isPause()) {
            recorder.setPause(true);
        }
    }

    @Override
    public void resumeRecording() {
        if (recorder == null) {
            startRecording();
        } else if (recorder.isPause()) {
            recorder.setPause(false);
        }
    }

    @Override
    public void reset() {
        if (recorder != null && recorder.isRecording()) {
            recorder.stop();
            audioWave.stopView();
        }
    }

    private void onRecordError() {
        FileUtils.deleteFile(filePath);
        filePath = "";
        reset();
        if (errorHandler != null) {
            errorHandler.onRecordError();
        }
    }

    @Override
    public boolean isRecording() {
        return recorder != null && !recorder.isPause();
    }

    @Override
    public void setFilePath(String path) {
        filePath = path;
    }

    public interface RecordErrorHandler {
        void onRecordError();
    }
}
