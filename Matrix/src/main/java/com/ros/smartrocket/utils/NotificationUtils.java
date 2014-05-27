package com.ros.smartrocket.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
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
    private static final String TAG = NotificationUtils.class.getSimpleName();
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
        String title = context.getString(R.string.file_waiting_to_upload);


        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        generateNotification(context, title, message, intent);
    }

    /**
     * Show notification about not uploaded file
     *
     * @param context    - current context
     * @param jsonObject - message Json object
     */
    public static void showTaskStatusChangedNotification(Context context, String jsonObject) {
        Intent intent = new Intent(context, MainActivity.class);
        String title = context.getString(R.string.app_name);
        String message = "";

        try {
            JSONObject messageObject = new JSONObject(jsonObject);
            int statusType = messageObject.optInt("StatusType");
            int surveyId = messageObject.optInt("SurveyId");
            int taskId = messageObject.optInt("TaskId");
            String taskName = messageObject.optString("TaskName");
            //String endDateTime = messageObject.optString("endDateTime");

            switch (statusType) {
                case 4: //Re-do
                    message = String.format(context.getString(R.string.re_do_task_status_message), taskName);
                    intent = IntentUtils.getQuestionsIntent(context, surveyId, taskId);
                    break;
                case 6: //Validated
                    message = String.format(context.getString(R.string.validated_task_status_message), taskName);
                    break;
                default:
                    message = String.format(context.getString(R.string.unknown_status_id_message),
                            String.valueOf(statusType));
                    break;
            }
        } catch (Exception e) {
            L.e(TAG, "ShowTaskStatusChangedNotification error" + e.getMessage(), e);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        generateNotification(context, title, message, intent);

    }

    public static Boolean generateNotification(Context context, String title, String message, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(Html.fromHtml(message)));

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(sound);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getInstance());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        return true;
    }
}
