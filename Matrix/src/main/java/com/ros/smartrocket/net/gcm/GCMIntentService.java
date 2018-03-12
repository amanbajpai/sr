package com.ros.smartrocket.net.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ros.smartrocket.App;
import com.ros.smartrocket.net.helper.MyTaskFetcher;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;

public class GCMIntentService extends JobIntentService {
    public static final int JOB_ID = 0x01;
    private static final String TAG = GCMIntentService.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private GcmManager gcmManager = GcmManager.getInstance();

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GCMIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Bundle extras = intent.getExtras();
        String messageType = gcmManager.getGcm().getMessageType(intent);
        if (extras != null && !extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                L.i(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                L.i(TAG, "Received deleted messages notification");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                onMessage(getApplicationContext(), intent);
            }
        }
    }


    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String messageJsonObject = extras.getString("message");
            L.d(TAG, "Received message [message=" + messageJsonObject + "]");
            if (messageJsonObject != null) {
                if (!TextUtils.isEmpty(preferencesManager.getToken())) {
                    if (preferencesManager.getUsePushMessages() && messageJsonObject.contains("TaskName"))
                        NotificationUtils.showTaskStatusChangedNotification(context, messageJsonObject);
                    new MyTaskFetcher().getMyTasksFromServer();
                }
                if (App.getInstance() != null && App.getInstance().getMyAccount() != null
                        && App.getInstance().getMyAccount().getAllowPushNotification() != null
                        && App.getInstance().getMyAccount().getAllowPushNotification()
                        && messageJsonObject.contains("Subject")) {
                    NotificationUtils.showAndSavePushNotification(context, messageJsonObject);
                }
            }
        }
    }
}