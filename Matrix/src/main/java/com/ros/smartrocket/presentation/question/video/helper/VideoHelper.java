package com.ros.smartrocket.presentation.question.video.helper;

import android.content.Intent;

import static com.ros.smartrocket.utils.SelectVideoManager.OnVideoCompleteListener;

public interface VideoHelper {
    void showSelectVideoDialog();

    void startCamera();

    void startGallery();

    void setVideoCompleteListener(OnVideoCompleteListener listener);

    void onActivityResult(int requestCode, int resultCode, Intent intent);

}
