package com.ros.smartrocket.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public final class StorageManager {
    private static final String IMAGE_DIR = "Images";
    private static final String VIDEO_DIR = "Videos";

    public static File getImageStoreDir(Context context) {
        return getDir(context, IMAGE_DIR);
    }

    public static File getVideoStoreDir(Context context) {
        return getDir(context, VIDEO_DIR);
    }

    private static File getDir(Context context, String folder) {
        String state = Environment.getExternalStorageState();

        File storageDir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            storageDir = new File(context.getExternalFilesDir(null), folder);
        } else {
            storageDir = new File(context.getFilesDir(), folder);
        }

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return storageDir;
    }
}