package com.ros.smartrocket.interfaces;

public interface QuestionAudioRecorder {
    void startRecording();

    void stopRecording();

    void pauseRecording();

    void resumeRecording();

    void reset();

    boolean isRecording();

    void setFilePath(String path);


}
