package com.ros.smartrocket.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.utils.UIUtils;

public class GlobalReceiver extends BroadcastReceiver {
    public static final String TAG = "GlobalReceiver";
    private static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (UIUtils.isOnline(context)) {
                if (firstConnect) {
                    firstConnect = false;
                    if (UploadFileService.canUploadNextFile(context)) {
                        context.startService(new Intent(context, UploadFileService.class).setAction(Keys.
                                ACTION_CHECK_NOT_UPLOADED_FILES));
                    }
                }
            } else {
                firstConnect = true;
            }

        } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startService(new Intent(context, UploadFileService.class).setAction(Keys.
                    ACTION_CHECK_NOT_UPLOADED_FILES));

        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            context.startService(new Intent(context, UploadFileService.class).setAction(Keys.
                    ACTION_CHECK_NOT_UPLOADED_FILES));
        }
    }
}