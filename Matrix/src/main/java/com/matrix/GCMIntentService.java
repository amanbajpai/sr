package com.matrix;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.matrix.net.gcm.CommonUtilities;
import com.matrix.utils.L;

import static com.matrix.net.gcm.CommonUtilities.displayMessage;

/**
 * GCM Services
 */
public class GCMIntentService extends GCMBaseIntentService {

    //@SuppressWarnings("hiding")
    private static final String TAG = GCMIntentService.class.getSimpleName();

    public GCMIntentService() {
        super(CommonUtilities.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        L.d(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered));
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        L.d(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            //TODO: Register on Matrix server
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
        L.d(TAG, "Received message [message=" + extras.get("message") + "]");
        displayMessage(context, message);
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        L.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        L.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        L.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        String title = context.getString(R.string.app_name);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(App.getInstance())
                        .setSmallIcon(icon)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(message);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        /* The stack builder object will contain an artificial back stack for the started Activity.
         This ensures that navigating backward from the Activity leads out of
         your application to the Home screen.*/
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getInstance());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        notificationManager.notify(0, mBuilder.build());
    }
}