package com.ros.smartrocket.interfaces;

public interface QuestionAudioPlayer {

    boolean isPlaying();

    void play();

    void stop();

    void pause();

    void reset();

    void setFilePath(String path);

}
