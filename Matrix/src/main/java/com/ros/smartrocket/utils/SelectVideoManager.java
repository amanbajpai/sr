package com.ros.smartrocket.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.ros.smartrocket.R;

import java.io.File;
import java.util.Calendar;

public class SelectVideoManager {
    private static final int GALLERY = 2016;
    private static final int CAMERA = 2026;
    private static SelectVideoManager instance = null;
    private OnVideoCompleteListener videoCompleteListener;
    private Dialog selectVideoDialog;
    private File lastFile;


    private SelectVideoManager() {
    }

    public static SelectVideoManager getInstance() {
        if (instance == null) {
            instance = new SelectVideoManager();
        }
        return instance;
    }

    public void startGallery(Fragment fragment) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        if (!IntentUtils.isIntentAvailable(fragment.getActivity(), i)) {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("video/*");
        }
        fragment.startActivityForResult(i, GALLERY);
    }

    public void startCamera(Fragment fragment) {
        lastFile = getTempFile(fragment.getActivity());
        Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, FileProcessingManager.getUriFromFile(lastFile));
        if (i.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(i, CAMERA);
        }
    }

    public Dialog showSelectVideoDialog(final Fragment fragment, final boolean showRemoveButton) {
        LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.select_image_dialog, null);
        v.findViewById(R.id.gallery).setOnClickListener(v1 -> {
            selectVideoDialog.dismiss();
            startGallery(fragment);
        });

        v.findViewById(R.id.camera).setOnClickListener(v12 -> {
            selectVideoDialog.dismiss();
            startCamera(fragment);
        });

        v.findViewById(R.id.remove).setVisibility(showRemoveButton ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.remove).setOnClickListener(v13 -> {
            selectVideoDialog.dismiss();
            videoCompleteListener.onVideoComplete(null);
        });

        v.findViewById(R.id.cancelButton).setOnClickListener(v14 -> selectVideoDialog.dismiss());

        final Dialog dialog = new Dialog(fragment.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(v);
        selectVideoDialog = dialog;
        dialog.show();

        return dialog;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK && (requestCode == CAMERA || requestCode == GALLERY)) {
            String path = lastFile.getPath();
            if (videoCompleteListener != null) {
                if (!TextUtils.isEmpty(path))
                    videoCompleteListener.onVideoComplete(path);
                else
                    videoCompleteListener.onSelectVideoError(requestCode);
            }
        }
    }

    public static File getTempFile(Context context) {
        File dir = StorageManager.getVideoStoreDir(context);
        return new File(dir, Calendar.getInstance().getTimeInMillis() + ".mp4");
    }

    public interface OnVideoCompleteListener {
        void onVideoComplete(String videoFilePath);

        void onSelectVideoError(int imageFrom);
    }

    public void setVideoCompleteListener(OnVideoCompleteListener videoCompleteListener) {
        this.videoCompleteListener = videoCompleteListener;
    }
}
