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
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class SelectVideoManager {
    private static final String TAG = "SelectImageManager";
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

    public void startGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        if (!IntentUtils.isIntentAvailable(activity, i)) {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("video/*");
        }
        activity.startActivityForResult(i, GALLERY);
    }

    public void startCamera() {
        lastFile = getTempFile(activity);

        Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(lastFile));
        if (i.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(i, CAMERA);
        }
    }

    public Dialog showSelectVideoDialog(final Activity activity, final boolean showRemoveButton) {
        this.activity = activity;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.select_image_dialog, null);
        v.findViewById(R.id.gallery).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoDialog.dismiss();
                startGallery();
            }
        });

        v.findViewById(R.id.camera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoDialog.dismiss();
                startCamera();
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
        return getVideoPathFromContentURI(activity, videoUri);
    }

    public static File copyFileToTempFolder(Context context, File file) {
        File resultFile = getTempFile(context);
        try {
            InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(resultFile);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultFile;
    }

    public static String getFileAsString(File file) {
        String resultString = "";
        try {
            byte[] fileAsBytesArray = FileUtils.readFileToByteArray(file);
            resultString = Base64.encodeToString(fileAsBytesArray, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
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
            L.e(TAG, "Error get Temp File");
        }
        return ret;
    }

    /*private void deleteTempFile() {
        try {
            File file = getTempFile(activity);
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            L.w(TAG, e.toString());
        }
    }*/

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
