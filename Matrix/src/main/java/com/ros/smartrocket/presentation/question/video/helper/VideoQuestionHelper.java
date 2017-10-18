package com.ros.smartrocket.presentation.question.video.helper;

import android.app.Activity;
import android.content.Intent;

import com.ros.smartrocket.utils.SelectVideoManager;

public class VideoQuestionHelper implements VideoHelper {
    private Activity activity;
    private SelectVideoManager selectVideoManager = SelectVideoManager.getInstance();

    public VideoQuestionHelper(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void showSelectVideoDialog() {
        selectVideoManager.showSelectVideoDialog(activity, true);
    }

    @Override
    public void startCamera() {
        selectVideoManager.startCamera(activity);
    }

    @Override
    public void startGallery() {
        selectVideoManager.startGallery(activity);
    }

    @Override
    public void setVideoCompleteListener(SelectVideoManager.OnVideoCompleteListener listener) {
        selectVideoManager.setVideoCompleteListener(listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        selectVideoManager.onActivityResult(requestCode, resultCode, intent);
    }
}
