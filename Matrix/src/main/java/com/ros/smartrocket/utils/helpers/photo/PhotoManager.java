package com.ros.smartrocket.utils.helpers.photo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;


import com.ros.smartrocket.ui.gallery.ImageDirectoryActivity;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;

public class PhotoManager implements PhotoHelper {
    private Fragment fragment;
    private SelectImageManager selectImageManager;


    public PhotoManager(Fragment fragment) {
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
    public void showSelectImageDialog(boolean showDeletePhoto, File file, int code) {
        if (fragment != null && fragment.isAdded()) {
            selectImageManager.showSelectImageDialog(fragment, showDeletePhoto, file, code);
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
    public void openGallery(Integer orderId, int imageListsize) {
        Intent intent = new Intent(fragment.getContext(), ImageDirectoryActivity.class);
        intent.putExtra("imageListsize",imageListsize);
        fragment.startActivityForResult(intent,65);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (fragment != null)
            selectImageManager.onActivityResult(requestCode, resultCode, intent, fragment.getContext());
    }
}
