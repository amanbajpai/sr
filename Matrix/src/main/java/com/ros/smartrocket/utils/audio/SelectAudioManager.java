package com.ros.smartrocket.utils.audio;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.StorageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by ankurrawal on 29/11/18.
 */


public class SelectAudioManager {
    private static final String TAG = SelectAudioManager.class.getSimpleName();
    private static final Random RANDOM = new Random();
    private static final int ONE_KB_IN_B = 1024;

    public static File getTempFile(Context context, @Nullable String prefix) {
        File dir = StorageManager.getAudioStoreDir(context);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "SmartRocket Audio");


        return new File(file, prefix + "_" + Calendar.getInstance().getTimeInMillis() + "_"
                + RANDOM.nextInt(Integer.MAX_VALUE) + ".mp3");


    }

    public static File copyFileToTempFolder(Context context, File file, String prefix) {
        File resultFile = getTempFile(context, prefix);

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(resultFile);

            // Transfer bytes from in to out
            byte[] buf = new byte[ONE_KB_IN_B];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (Exception e) {
            L.e(TAG, "CopyFileToTempFolder error" + e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                L.e(TAG, "GetScaledFile error" + e.getMessage(), e);
            }

        }
        return resultFile;
    }

}
