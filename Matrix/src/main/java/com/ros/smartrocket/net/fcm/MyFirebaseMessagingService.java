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

import org.json.JSONObject;

/**
 * Created by ankurrawal on 10/8/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            try {
                if (remoteMessage.getData().size() > 0) {
                    L.d(TAG, "Data payload==" + remoteMessage.getData());
                } else if (remoteMessage.getNotification() != null) {
                    L.d(TAG, "Notification payload==" + remoteMessage.getNotification().getBody());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            Log.d(TAG, "From: " + remoteMessage.getFrom());
            if (remoteMessage.getData() != null) {
                Log.e("push json", remoteMessage.getData().toString());

                JSONObject messageJsonObject = new JSONObject(remoteMessage.getData().toString());
                String message = messageJsonObject.optString("message");


                //String messageJsonObject = remoteMessage.getData().toString();

                L.d(TAG, "Received message [message=" + message + "]");
                if (message != null) {
                    if (!TextUtils.isEmpty(preferencesManager.getToken())) {
                        if (preferencesManager.getUsePushMessages() && message.contains("TaskName"))
                            NotificationUtils.showTaskStatusChangedNotification(this, message);
                        new MyTaskFetcher().getMyTasksFromServer();
                    }
                    if (App.getInstance() != null && App.getInstance().getMyAccount() != null
                            && App.getInstance().getMyAccount().getAllowPushNotification() != null
                            && App.getInstance().getMyAccount().getAllowPushNotification()
                            && message.contains("Subject")) {

                        NotificationUtils.showAndSavePushNotification(this, message);
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



