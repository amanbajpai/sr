package com.ros.smartrocket.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import org.json.JSONObject;

import java.io.File;

/**
 * Utils class for easy work with UI Views
 */
public class NotificationUtils {
    //private static final String TAG = "NotificationUtils";
    public static final int NOTIFICATION_ID = 1;

    /**
     * Show notification about not uploaded file
     *
     * @param context         - current context
     * @param notUploadedFile - info about file to upload
     */
    public static void sendNotUploadedFileNotification(final Context context, NotUploadedFile notUploadedFile) {
        File file = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
        String message = "File: " + file.getName() + " from task Id: " + notUploadedFile.getTaskId() + " is "
                + "waiting to upload";

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);
        mBuilder.setContentTitle(context.getResources().getString(R.string.file_waiting_to_upload));
        mBuilder.setContentText(message);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(sound);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getInstance());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * Show upload file progress notification
     *
     * @param context
     */
    /*public static void sendNotUploadedFileNotification(final Context context, FileToUpload fileToUpload, int progress) {

        File file = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
        String message = "File: " + file.getName() + " from task Id: " + notUploadedFile.getTaskId() + " is " +
                "waiting to upload";

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);
        mBuilder.setContentTitle(context.getResources().getString(R.string.file_waiting_to_upload));
        mBuilder.setContentText(message);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(sound);

        Intent intent = new Intent(context, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }*/

    /**
     * Show notification about not uploaded file
     *
     * @param context    - current context
     * @param jsonObject - message Json object
     */
    public static void showTaskStatusChangedNotification(Context context, String jsonObject) {
        String message = "";

        try {
            JSONObject messageObject = new JSONObject(jsonObject);
            int statusType = messageObject.optInt("StatusType");
            //int surveyId = messageObject.optInt("SurveyId");
            //int taskId = messageObject.optInt("TaskId");
            String taskName = messageObject.optString("TaskName");
            //String endDateTime = messageObject.optString("endDateTime");

            switch (statusType) {
                case 4: //Re-do
                    message = String.format(context.getString(R.string.re_do_task_status_message), taskName);
                    break;
                case 6: //Validated
                    message = String.format(context.getString(R.string.validated_task_status_message), taskName);
                    break;
                default:
                    message = String.format(context.getString(R.string.unknown_status_id_message), String.valueOf(statusType));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);
        mBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
        mBuilder.setContentText(message);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(sound);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getInstance());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
