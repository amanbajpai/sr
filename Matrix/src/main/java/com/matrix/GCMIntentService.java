package com.matrix;

import android.content.Context;
import android.content.Intent;
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
        //TODO: Register on Matrix server
        //ServerUtilities serverUtilities = new ServerUtilities(this);
        //serverUtilities.makeRequestRegister(registrationId);
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
        L.i(TAG, "Received message");
        String message = intent.getStringExtra("message");
        String voucherId = intent.getStringExtra("voucherId");
        displayMessage(context, message);
        generateNotification(context, message, voucherId);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        L.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        generateNotification(context, message, "");
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
    private static void generateNotification(Context context, String message, String voucherId) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();

        // TODO: Show notification
//        if (MainActivity.isAppRunning) {
//            Intent notifyNew = new Intent(context, MainActivity.class);
//            notifyNew.setAction(MainActivity.ACTION_SHOW_NEW);
//            notifyNew.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                    Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(notifyNew);
//        } else {
//            NotificationManager notificationManager = (NotificationManager)
//                    context.getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notification = new Notification(icon, message, when);
//            String title = context.getString(R.string.friends_tip_app_name);
//            Intent notificationIntent = new Intent(context, MainActivity.class);
//            notificationIntent.putExtra(Util.VOUCHER_ID, voucherId);
//            notificationIntent.setAction(MainActivity.ACTION_SHOW_LIST);
//            // set intent so it does not start a new activity
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//            notification.setLatestEventInfo(context, title, message, intent);
//            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//            notificationManager.notify(0, notification);
//        }

    }
}