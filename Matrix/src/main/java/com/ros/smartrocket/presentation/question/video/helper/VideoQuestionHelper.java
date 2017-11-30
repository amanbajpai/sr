package com.ros.smartrocket.presentation.question.video.helper;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.ros.smartrocket.utils.SelectVideoManager;

public class VideoQuestionHelper implements VideoHelper {
    private Fragment fragment;
    private SelectVideoManager selectVideoManager = SelectVideoManager.getInstance();

    public VideoQuestionHelper(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void showSelectVideoDialog() {
        selectVideoManager.showSelectVideoDialog(fragment, true);
    }

    @Override
    public void startCamera() {
        selectVideoManager.startCamera(fragment);
    }

    @Override
    public void startGallery() {
        selectVideoManager.startGallery(fragment);
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
