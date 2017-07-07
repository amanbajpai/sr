package com.ros.smartrocket.interfaces;

public interface QuestionAudioPlayer {

    boolean isPlaying();

    void play();

    void pause();

    void setFilePath(String path);

}
