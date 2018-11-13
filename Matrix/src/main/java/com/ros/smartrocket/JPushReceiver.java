package com.ros.smartrocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.net.helper.FcmRegistrar;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.schedulers.Schedulers;

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = JPushReceiver.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            L.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String registrationId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] Registration Id : " + registrationId);

                if (Config.USE_BAIDU) {
                    L.d(TAG, "[MyReceiver] Send registered to server: regId = " + registrationId);
                    new FcmRegistrar().registerFCMId(registrationId, 1);
//                    preferencesManager.setFCMRegistrationId(registrationId);
                }

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "ACTION_NOTIFICATION_RECEIVED");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "notifactionId: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "ACTION_NOTIFICATION_OPENED");

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "ACTION_RICHPUSH_CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "Unhandled intent - " + intent.getAction());
            }
        }
    }

    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
                } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
                } else {
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getString(key));
                }
            }
        }
        return sb.toString();
    }

    private void processCustomMessage(Context context, Bundle bundle) {
        String messageJsonObject = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (messageJsonObject != null) {
            if (!TextUtils.isEmpty(preferencesManager.getToken())) {
                if (preferencesManager.getUsePushMessages()
                        && messageJsonObject.contains("TaskName")) {
                    NotificationUtils.showTaskStatusChangedNotification(context, messageJsonObject);
                }
                getMyTasksFromServer();
            }

            if (App.getInstance().getMyAccount() != null
                    && App.getInstance().getMyAccount().getAllowPushNotification()
                    && messageJsonObject.contains("Subject")) {
                NotificationUtils.showAndSavePushNotification(context, messageJsonObject);
            }
        }
    }

    private void getMyTasksFromServer() {
        App.getInstance().getApi()
                .getMyTasks(preferencesManager.getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(w -> new WavesStore().storeMyWaves(w))
                .doOnError(this::handleNetworkError)
                .subscribe();
    }

    private void handleNetworkError(Throwable throwable) {
        L.i(TAG, "Network error");
    }
}
