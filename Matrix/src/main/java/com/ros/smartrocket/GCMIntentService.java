package com.ros.smartrocket;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.gcm.CommonUtilities;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;

/**
 * GCM Services
 */
public class GCMIntentService extends GCMBaseIntentService {

    //@SuppressWarnings("hiding")
    private static final String TAG = GCMIntentService.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();

    public GCMIntentService() {
        super(CommonUtilities.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        L.d(TAG, "Device registered: regId = " + registrationId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_registered));
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        L.d(TAG, "Device unregistered");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            L.i(TAG, "Register on Matrix server");
//            ServerUtilities serverUtilities = new ServerUtilities(this);
//            serverUtilities.makeRequestUnregister();
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            L.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String message = extras.getString("message");
        L.d(TAG, "Received message [message=" + message + "]");

        NotificationUtils.showTaskStatusChangedNotification(context, message);
        apiFacade.sendRequest(context, apiFacade.getMyTasksOperation());

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
