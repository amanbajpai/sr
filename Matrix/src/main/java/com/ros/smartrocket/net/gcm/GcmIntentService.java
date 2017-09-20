package com.ros.smartrocket.net.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ros.smartrocket.net.retrofit.helper.MyTaskFetcher;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.helpers.APIFacade;

public class GcmIntentService extends IntentService {
    private static final String TAG = GcmIntentService.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private GcmManager gcmManager = GcmManager.getInstance();

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String messageType = gcmManager.getGcm().getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                L.i(TAG, "Send error: " + extras.toString());

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                L.i(TAG, "Received deleted messages notification");

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String messageJsonObject = extras.getString("message");
                L.d(TAG, "Received message [message=" + messageJsonObject + "]");

                if (!TextUtils.isEmpty(preferencesManager.getToken())) {
                    if (preferencesManager.getUsePushMessages()) {
                        NotificationUtils.showTaskStatusChangedNotification(this, messageJsonObject);
                    }
                    new MyTaskFetcher().getMyTasksFromServer();
                }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


}