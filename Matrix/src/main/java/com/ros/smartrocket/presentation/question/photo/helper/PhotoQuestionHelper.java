package com.ros.smartrocket.presentation.question.photo.helper;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;

public class PhotoQuestionHelper implements PhotoHelper {
    private Fragment fragment;
    private SelectImageManager selectImageManager;

    public PhotoQuestionHelper(Fragment fragment) {
        this.fragment = fragment;
        selectImageManager = new SelectImageManager();
    }

    @Override
    public File getTempFile(String path) {
        return SelectImageManager.getTempFile(fragment.getContext(), path);
    }

    @Override
    public void startCamera(File file, Integer orderId) {
        if (fragment != null && fragment.isAdded()) {
            SelectImageManager.startCamera(fragment, file, orderId);
        }
    }

    @Override
    public void showSelectImageDialog(String path) {
        if (fragment != null && fragment.isAdded()) {
            File fileToPhoto = SelectImageManager.getTempFile(fragment.getContext(), path);
            selectImageManager.showSelectImageDialog(fragment, true, fileToPhoto);
        }
    }

    @Override
    public void showFullScreenImage(String path) {
        if (!TextUtils.isEmpty(path) && fragment != null && fragment.isAdded()) {
            Intent intent = IntentUtils.getFullScreenImageIntent(fragment.getContext(), path);
            fragment.getActivity().startActivity(intent);
        }
    }

    @Override
    public void startGallery(Integer orderId) {
        if (fragment != null && fragment.isAdded()) {
            SelectImageManager.startGallery(fragment, orderId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (fragment != null)
            selectImageManager.onActivityResult(requestCode, resultCode, intent, fragment.getContext());
    }
}