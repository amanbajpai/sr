package com.ros.smartrocket.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

public class GlobalReceiver extends BroadcastReceiver {
    public static final String TAG = "GlobalReceiver";
    private boolean firstConnect = true;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (UIUtils.isOnline(context)) {
                if (firstConnect) {
                    firstConnect = false;

                    startUploadFileService(context);
                }
            } else {
                firstConnect = true;
            }

        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())
                || Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startUploadFileService(context);
            startTaskReminderService(context);
        }
    }

    public void startTaskReminderService(Context context) {
        if (preferencesManager.getUsePushMessages() || preferencesManager.getUseDeadlineReminder()) {
            context.startService(new Intent(context, TaskReminderService.class).setAction(Keys
                    .ACTION_START_REMINDER_TIMER));
        } else {
            context.stopService(new Intent(context, TaskReminderService.class));
        }
    }

    public void startUploadFileService(Context context) {
        if (UploadFileService.canUploadNextFile(context)) {
            context.startService(new Intent(context, UploadFileService.class).setAction(Keys.
                    ACTION_CHECK_NOT_UPLOADED_FILES));
        }
    }
}