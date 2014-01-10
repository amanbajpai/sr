package com.ros.smartrocket.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

public class GlobalReceiver extends BroadcastReceiver {
    public static final String TAG = "GlobalReceiver";
    private static boolean firstConnect = true;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (UIUtils.isOnline(context)) {
                if (firstConnect) {
                    firstConnect = false;
                    if (UploadFileService.canUploadNextFile(context)) {
                        context.startService(new Intent(context, UploadFileService.class).setAction(Keys.ACTION_CHECK_NOT_UPLOADED_FILES));
                    }
                }
            } else {
                firstConnect = true;
            }

        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            context.startService(new Intent(context, UploadFileService.class).setAction(Keys.ACTION_CHECK_NOT_UPLOADED_FILES));
        }
    }
}