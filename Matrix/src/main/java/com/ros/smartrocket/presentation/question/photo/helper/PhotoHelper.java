package com.ros.smartrocket.presentation.question.photo.helper;

import android.content.Intent;

import java.io.File;

public interface PhotoHelper {

    File getTempFile(String path);

    void startCamera(File file, Integer orderId);

    void showSelectImageDialog(String path);

    void showFullScreenImage(String path);

    void startGallery(Integer orderId);

    void onActivityResult(int requestCode, int resultCode, Intent intent);
}