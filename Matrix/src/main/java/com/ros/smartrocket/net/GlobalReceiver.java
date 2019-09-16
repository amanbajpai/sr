package com.ros.smartrocket.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.WorkManager.UploadFileServiceWorker;
import com.ros.smartrocket.WorkManager.WorkManagerScheduler;
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


            WorkManagerScheduler.callWorkManager(Keys.ACTION_START_REMINDER_TIMER);


        } else {

            WorkManagerScheduler.callWorkManager(Keys.ACTION_STOP_REMINDER_TIMER);

        }
    }

    public void startUploadFileService(Context context) {
        if (UploadFileServiceWorker.canUploadNextFile(context)) {

            WorkManagerScheduler.callWorkManager(Keys.ACTION_CHECK_NOT_UPLOADED_FILES);

        }

    }
}