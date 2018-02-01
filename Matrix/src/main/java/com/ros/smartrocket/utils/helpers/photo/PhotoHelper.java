package com.ros.smartrocket.utils.helpers.photo;

import android.content.Intent;

import java.io.File;

public interface PhotoHelper {

    File getTempFile(String path);

    void startCamera(File file, Integer orderId);

    void showSelectImageDialog(final boolean showRemoveButton, final File file, int code);

    void showFullScreenImage(String path);

    void startGallery(Integer orderId);

    void onActivityResult(int requestCode, int resultCode, Intent intent);
}
