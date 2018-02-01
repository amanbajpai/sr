package com.ros.smartrocket.interfaces;

public interface PhotoActionsListener {
    void addPhoto(int fieldId);

    void onPhotoClicked(String url, int fieldId);
}
