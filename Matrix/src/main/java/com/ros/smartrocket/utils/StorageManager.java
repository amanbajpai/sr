package com.ros.smartrocket.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public final class StorageManager {
    private static final String IMAGE_DIR = "Images";
    private static final String VIDEO_DIR = "Videos";
    private static final String AUDIO_DIR = "Audios";
    private static final String APP_AUDIO_DIR = "SmartRocketAudio";
    private static final String APP_VIDEO_DIR = "SmartRocketVideo";
    private static final String APP_IMAGES_DIR = "SmartRocketImages";

    /// ======================================================================================================== ///
    /// ================================================= PUBLIC =============================================== ///
    /// ======================================================================================================== ///

    public static File getImageStoreDir(Context context) {
        return getDir(context, IMAGE_DIR);
    }

    public static File getVideoStoreDir(Context context) {
        return getDir(context, VIDEO_DIR);
    }

    public static File getImageCacheDir(Context context) {
        return getCacheDir(context, IMAGE_DIR);
    }

    public static String getAudioCacheDirPath(Context context) {
        return getCacheDir(context, AUDIO_DIR).getAbsolutePath();
    }

    public static File getAudioStoreDir(Context context) {
        return getDir(context, AUDIO_DIR);
    }

    /// ======================================================================================================== ///
    /// ================================================ PRIVATE =============================================== ///
    /// ======================================================================================================== ///

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

    private static File getCacheDir(Context context, String folder) {
        String state = Environment.getExternalStorageState();

        File storageDir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            storageDir = new File(context.getExternalCacheDir(), folder);
        } else {
            storageDir = new File(context.getCacheDir(), folder);
        }

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return storageDir;
    }

    public static File getMusicDir() {
        File storageDir;
        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File mDir = new File(storageDir, APP_AUDIO_DIR);
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        return mDir;
    }

    public static File getImageDir() {
        File storageDir;
        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File mDir = new File(storageDir, APP_IMAGES_DIR);
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        return mDir;
    }

    public static File getVideoDir() {
        File storageDir;
        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File mDir = new File(storageDir, APP_VIDEO_DIR);
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        return mDir;
    }


}