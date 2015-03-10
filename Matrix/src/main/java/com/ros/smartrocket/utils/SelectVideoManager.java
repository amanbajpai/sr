package com.ros.smartrocket.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;

import java.io.File;
import java.util.Calendar;

public class SelectVideoManager {
    private static final String TAG = SelectVideoManager.class.getSimpleName();
    public static final int GALLERY = 201;
    public static final int CAMERA = 202;

    private static SelectVideoManager instance = null;
    private OnVideoCompleteListener videoCompleteListener;
    private Activity activity;

    private Dialog selectVideoDialog;
    private File lastFile;

    public static SelectVideoManager getInstance() {
        if (instance == null) {
            instance = new SelectVideoManager();
        }
        return instance;
    }

    public SelectVideoManager() {
    }

    /**
     * Get video from Gallery
     */

    public void startGallery(Activity activity) {
        this.activity = activity;

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        if (!IntentUtils.isIntentAvailable(activity, i)) {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("video/*");
        }
        activity.startActivityForResult(i, GALLERY);
    }

    /**
     * Use camera for getting video
     */
    public void startCamera(Activity activity) {
        this.activity = activity;

        lastFile = getTempFile(activity);

        Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(lastFile));
        if (i.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(i, CAMERA);
        }
    }

    /**
     * Show dialog for selection
     */
    public Dialog showSelectVideoDialog(final Activity activity, final boolean showRemoveButton) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.select_image_dialog, null);
        v.findViewById(R.id.gallery).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoDialog.dismiss();
                startGallery(activity);
            }
        });

        v.findViewById(R.id.camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoDialog.dismiss();
                startCamera(activity);
            }
        });

        v.findViewById(R.id.remove).setVisibility(showRemoveButton ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.remove).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoDialog.dismiss();
                videoCompleteListener.onVideoComplete(null);
            }
        });

        v.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoDialog.dismiss();
            }
        });

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(v);
        selectVideoDialog = dialog;
        dialog.show();

        return dialog;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            String path = null;
            if (requestCode == SelectVideoManager.GALLERY) {
                path = getVideoPathFromGallery(intent);

            } else if (requestCode == CAMERA) {
                path = getVideoPathFromCamera(intent);
            }

            if (videoCompleteListener != null) {
                if (!TextUtils.isEmpty(path)) {
                    videoCompleteListener.onVideoComplete(path);
                } else {
                    videoCompleteListener.onSelectVideoError(requestCode);
                }
            }
        }
    }

    public String getVideoPathFromGallery(Intent intent) {
        Uri videoUri = intent.getData();
        return getVideoPathFromContentURI(activity, videoUri);
    }

    public String getVideoPathFromCamera(Intent intent) {
        Uri videoUri = intent.getData();
        if (videoUri != null) {
            return getVideoPathFromContentURI(activity, videoUri);
        } else {
            return null;
        }
    }

    public static File getTempFile(Context context) {
        File ret = null;
        try {
            String state = Environment.getExternalStorageState();

            File cacheDir;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                cacheDir = new File(Config.CACHE_DIR, "videos");
            } else {
                cacheDir = context.getFilesDir();
            }

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                ret = new File(cacheDir, Calendar.getInstance().getTimeInMillis() + ".mp4");
            } else {
                ret = new File(cacheDir + "/", Calendar.getInstance().getTimeInMillis() + ".mp4");
            }
        } catch (Exception e) {
            L.e(TAG, "Error get Temp File", e);
        }
        return ret;
    }

    public static String getVideoPathFromContentURI(Activity activity, Uri contentUri) {
        String[] column = {MediaStore.Video.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(contentUri, column, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        return cursor.getString(columnIndex);
    }

    public File getLastFile() {
        return lastFile;
    }

    public interface OnVideoCompleteListener {
        void onVideoComplete(String videoFilePath);

        void onSelectVideoError(int imageFrom);
    }

    public OnVideoCompleteListener getVideoCompleteListener() {
        return videoCompleteListener;
    }

    public void setVideoCompleteListener(OnVideoCompleteListener videoCompleteListener) {
        this.videoCompleteListener = videoCompleteListener;
    }
}
