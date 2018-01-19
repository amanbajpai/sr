package com.ros.smartrocket.interfaces;

public interface PhotoActionsListener {
    void addPhoto();

    void deletePhoto();

    void onPhotoClicked(String url);
}
