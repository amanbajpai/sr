package com.ros.smartrocket.net.fcm;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ros.smartrocket.App;
import com.ros.smartrocket.net.helper.MyTaskFetcher;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;

/**
 * Created by ankurrawal on 10/8/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            //TODO handle notification

            Log.d(TAG, "From: " + remoteMessage.getFrom());
            if (remoteMessage.getData() != null) {
                Log.e("push json", remoteMessage.getData().toString());
                String messageJsonObject = remoteMessage.getData().toString();
                L.d(TAG, "Received message [message=" + messageJsonObject + "]");
                if (messageJsonObject != null) {
                    if (!TextUtils.isEmpty(preferencesManager.getToken())) {
                        if (preferencesManager.getUsePushMessages() && messageJsonObject.contains("TaskName"))
                            NotificationUtils.showTaskStatusChangedNotification(this, messageJsonObject);
                        new MyTaskFetcher().getMyTasksFromServer();
                    }
                    if (App.getInstance() != null && App.getInstance().getMyAccount() != null
                            && App.getInstance().getMyAccount().getAllowPushNotification() != null
                            && App.getInstance().getMyAccount().getAllowPushNotification()
                            && messageJsonObject.contains("Subject")) {
                        NotificationUtils.showAndSavePushNotification(this, messageJsonObject);
                    }
                }
            } else {
                Log.d(TAG, "Notification Message Body: " + "Body is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
