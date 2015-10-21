package com.ros.smartrocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.gcm.CommonUtilities;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;

/**
 * GCM Services
 */
public class GCMIntentService extends GCMBaseIntentService {

    //@SuppressWarnings("hiding")
    private static final String TAG = GCMIntentService.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();

    public GCMIntentService() {
        super(Config.GCM_SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        L.d(TAG, "Device registered: regId = " + registrationId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_registered));

        if(!Config.USE_BAIDU) {
            L.d(TAG, "Send registered to server: regId = " + registrationId);
            APIFacade.getInstance().registerGCMId(App.getInstance(), registrationId, 0);
            PreferencesManager.getInstance().setGCMRegistrationId(registrationId);
        }
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        L.d(TAG, "Device unregistered");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            L.i(TAG, "Register on Matrix server");
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            L.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String messageJsonObject = extras.getString("message");
        L.d(TAG, "Received message [message=" + messageJsonObject + "]");

        if (!TextUtils.isEmpty(preferencesManager.getToken())) {
            if (preferencesManager.getUsePushMessages() && messageJsonObject.contains("TaskName")) {
                NotificationUtils.showTaskStatusChangedNotification(context, messageJsonObject);
            }

            if (App.getInstance().getMyAccount().getAllowPushNotification()
                    && messageJsonObject.contains("Subject")){
                NotificationUtils.showAndSavePushNotification(context, messageJsonObject);
            }
            apiFacade.sendRequest(context, apiFacade.getMyTasksOperation());
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        L.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        String title = context.getString(R.string.app_name);

        CommonUtilities.displayMessage(context, message);

        Intent intent = new Intent(context, MainActivity.class);
        NotificationUtils.generateNotification(context, title, message, intent);
    }

    @Override
    public void onError(Context context, String errorId) {
        L.i(TAG, "Received error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
    }


    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        L.i(TAG, "Received recoverable error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }
}
