package com.ros.smartrocket.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.activity.MainActivity;
import com.ros.smartrocket.ui.activity.NotificationActivity;
import com.ros.smartrocket.ui.activity.PushNotificationActivity;
import com.ros.smartrocket.bl.NotificationBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.CustomNotificationStatus;
import com.ros.smartrocket.db.entity.Notification;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.service.CleanFilesIntentService;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Utils class for easy work with UI Views
 */
public class NotificationUtils {
    private static final String TAG = NotificationUtils.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;

    /**
     * Start popup-notification about not uploaded files
     *
     * @param context     - current context
     * @param missionName - current missionName
     */
    public static void startFileNotUploadedNotificationActivity(Context context, String missionName) {

        Spanned notificationText = Html.fromHtml(context.getString(R.string.not_uploaded_notification_text, missionName));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.NOTIFICATION_TYPE_ID, NotificationActivity.NotificationType.mission_expired.getId());
        intent.putExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, R.color.red);
        intent.putExtra(Keys.TITLE_ICON_RES_ID, R.drawable.info_icon);
        intent.putExtra(Keys.NOTIFICATION_TITLE, context.getString(R.string.not_uploaded_notification_title));
        intent.putExtra(Keys.NOTIFICATION_TEXT, notificationText);
        intent.putExtra(Keys.RIGHT_BUTTON_RES_ID, R.string.not_uploaded_notification_ok);

        context.startActivity(intent);
    }

    /**
     * Start popup-notification about expired task
     *
     * @param context        - current context
     * @param missionName    - current missionName
     * @param locationName   - current locationName
     * @param missionAddress - current missionAddress
     */
    public static void startExpiredNotificationActivity(Context context, String missionName,
                                                        String locationName, String missionAddress) {

        Spanned notificationText = Html.fromHtml(context.getString(R.string.expire_mission_notification_text, missionName,
                locationName, missionAddress));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.NOTIFICATION_TYPE_ID, NotificationActivity.NotificationType.mission_expired.getId());
        intent.putExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, R.color.red);
        intent.putExtra(Keys.TITLE_ICON_RES_ID, R.drawable.info_icon);
        intent.putExtra(Keys.NOTIFICATION_TITLE, context.getString(R.string.expire_mission_notification_title));
        intent.putExtra(Keys.NOTIFICATION_TEXT, notificationText);
        intent.putExtra(Keys.RIGHT_BUTTON_RES_ID, R.string.ok);

        context.startActivity(intent);
    }

    /**
     * Show notification about changed task status
     *
     * @param context    - current context
     * @param jsonObject - message Json object
     */
    public static void showTaskStatusChangedNotification(Context context, String jsonObject) {

        try {
            Task task = new Task();
            JSONObject messageObject = new JSONObject(jsonObject);
            task.setId(messageObject.optInt("TaskId"));
            task.setMissionId(messageObject.optInt("MissionId"));
            task.setStatusId(messageObject.optInt("StatusType"));
            task.setWaveId(messageObject.optInt("WaveId"));
            task.setName(getStringForCurrentLang(messageObject.optJSONObject("TaskName")));

            String presetValidationText = getStringForCurrentLang(messageObject.optJSONObject("PresetValidationText"));
            if (!TextUtils.isEmpty(presetValidationText)) {
                presetValidationText = "<br><br>" + presetValidationText;
            } else {
                presetValidationText = "";
            }

            String validationText = messageObject.optString("ValidationText");
            if (!TextUtils.isEmpty(validationText)) {
                validationText = "<br><br>" + validationText;
            } else {
                validationText = "";
            }
            task.setLocationName(messageObject.optString("LocationName"));
            task.setAddress(messageObject.optString("MissionAddress"));
            //String endDateTime = messageObject.optString("endDateTime");

            switch (TasksBL.getTaskStatusType(task.getStatusId())) {
                case RE_DO_TASK:
                    NotificationUtils.startRedoNotificationActivity(context, task);
                    break;
                case VALIDATED:
                    NotificationUtils.startApprovedNotificationActivity(context, presetValidationText, validationText, task);
                    CleanFilesIntentService.start(context, String.valueOf(task.getId()));
                    break;
                case REJECTED:
                    NotificationUtils.startRejectNotificationActivity(context, presetValidationText, validationText, task);
                    CleanFilesIntentService.start(context, String.valueOf(task.getId()));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            L.e(TAG, "ShowTaskStatusChangedNotification error" + e.getMessage(), e);
        }
    }

    /**
     * Show push notification
     *
     * @param context    - current context
     * @param jsonObject - message Json object
     */
    public static void showAndSavePushNotification(Context context, String jsonObject) {

        try {
            Gson gson = new Gson();
            Notification notification = gson.fromJson(jsonObject, Notification.class);

            NotificationBL.saveNotification(context.getContentResolver(), notification);

            List<Notification> notifications = NotificationBL.convertCursorToNotificationList(NotificationBL.getUnreadNotificationsFromDB(context.getContentResolver()));
            PreferencesManager.getInstance().setShowPushNotifStar(true);

            if (!PreferencesManager.getInstance().getToken().isEmpty()) {
                if (notifications.size() == 1) {
                    Intent intent = new Intent(context, PushNotificationActivity.class);
                    generateNotification(context, notification.getSubject(), Html.fromHtml(notification.getMessage()).toString(), intent);
                } else {
                    String title = context.getString(R.string.new_push_notifications_header);
                    String body = context.getString(R.string.new_push_notifications_body);
                    Intent intent = new Intent(context, PushNotificationActivity.class);
                    generateNotification(context, title, body, intent);
                }
            } else {
                String title = context.getString(R.string.new_push_notification);
                String body = new String();
                Intent intent = new Intent(context, PushNotificationActivity.class);
                generateNotification(context, title, body, intent);
            }

            IntentUtils.refreshPushNotificationsList(context);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Get localized string
     *
     * @param stringJsonObject - json object to separate
     */

    public static String getStringForCurrentLang(JSONObject stringJsonObject) {
        String resultString = "";
        try {
            String languageCode = PreferencesManager.getInstance().getLanguageCode();

            if (stringJsonObject.has(languageCode)) {
                resultString = stringJsonObject.optString(languageCode);
            } else {
                Iterator iterator = stringJsonObject.keys();
                resultString = stringJsonObject.optString((String) iterator.next());
            }
        } catch (Exception e) {
            L.e(TAG, "Parse object Error: " + e.getMessage(), e);
        }

        return resultString;
    }

    /**
     * Start popup-notification about redo task
     *
     * @param context - current context
     * @param task    - current task
     */
    public static void startRedoNotificationActivity(Context context, Task task) {

        Spanned notificationText = Html.fromHtml(context.getString(R.string.redo_mission_notification_text,
                task.getName(), task.getLocationName(), task.getAddress(), task.getId()));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.WAVE_ID, task.getWaveId());
        intent.putExtra(Keys.TASK_ID, task.getId());
        intent.putExtra(Keys.MISSION_ID, task.getMissionId());

        intent.putExtra(Keys.NOTIFICATION_TYPE_ID, NotificationActivity.NotificationType.mission_redo.getId());
        intent.putExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, R.color.orange_dark);
        intent.putExtra(Keys.TITLE_ICON_RES_ID, R.drawable.info_icon);
        intent.putExtra(Keys.NOTIFICATION_TITLE, context.getString(R.string.redo_mission_notification_title));
        intent.putExtra(Keys.NOTIFICATION_TEXT, notificationText);
        intent.putExtra(Keys.LEFT_BUTTON_RES_ID, R.string.close_message);
        intent.putExtra(Keys.RIGHT_BUTTON_RES_ID, R.string.open_mission);

        context.startActivity(intent);
    }

    /**
     * Start popup-notification about approved task
     *
     * @param context        - current context
     * @param validationText - current validationText
     */
    public static void startApprovedNotificationActivity(Context context, String presetValidationText,
                                                         String validationText, Task task) {
        context.startActivity(getApprovedNotificationIntent(context, presetValidationText,
                validationText, task, true));
    }

    public static Intent getApprovedNotificationIntent(Context context, String presetValidationText,
                                                       String validationText, Task task, boolean showLeftButton) {

        Spanned notificationText = Html.fromHtml(context.getString(R.string.approved_mission_notification_text,
                presetValidationText, validationText, task.getName(), task.getLocationName(),
                task.getAddress(), task.getId()));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.NOTIFICATION_TYPE_ID, NotificationActivity.NotificationType.mission_approved.getId());
        intent.putExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, R.color.green);
        intent.putExtra(Keys.TITLE_ICON_RES_ID, R.drawable.confirm_icon);
        intent.putExtra(Keys.NOTIFICATION_TITLE, context.getString(R.string.approved_mission_notification_title));
        intent.putExtra(Keys.NOTIFICATION_TEXT, notificationText);
        intent.putExtra(Keys.RIGHT_BUTTON_RES_ID, R.string.ok);
        intent.putExtra(Keys.SHOW_LEFT_BUTTON, showLeftButton);
        return intent;
    }

    /**
     * Start popup-notification about deadline task
     *
     * @param context        - current context
     * @param deadlineTime   - current deadlineTime
     * @param waveId         - current waveId
     * @param taskId         - current taskId
     * @param missionName    - current missionName
     * @param locationName   - current locationName
     * @param missionAddress - current missionAddress
     */
    public static void startDeadlineNotificationActivity(Context context, long deadlineTime, int waveId, int taskId,
                                                         int missionId, String missionName,
                                                         String locationName, String missionAddress, int taskStatusId) {
        String deadlineDateText = UIUtils.longToString(deadlineTime, 3);

        Spanned notificationText = Html.fromHtml(context.getString(R.string.deadline_mission_notification_text,
                deadlineDateText, missionName, locationName, missionAddress));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.WAVE_ID, waveId);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);

        intent.putExtra(Keys.NOTIFICATION_TYPE_ID, NotificationActivity.NotificationType.mission_deadline.getId());
        intent.putExtra(Keys.TASK_STATUS_ID, taskStatusId);
        intent.putExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, R.color.orange_dark);
        intent.putExtra(Keys.TITLE_ICON_RES_ID, R.drawable.info_icon);
        intent.putExtra(Keys.NOTIFICATION_TITLE, context.getString(R.string.deadline_mission_notification_title));
        intent.putExtra(Keys.NOTIFICATION_TEXT, notificationText);
        intent.putExtra(Keys.LEFT_BUTTON_RES_ID, R.string.close_message);
        intent.putExtra(Keys.RIGHT_BUTTON_RES_ID, R.string.open_mission);

        context.startActivity(intent);
    }


    /**
     * Start popup-notification about reject task
     *
     * @param context        - current context
     * @param validationText - current validationText
     * @param task           - current task
     */
    public static void startRejectNotificationActivity(Context context, String presetValidationText,
                                                       String validationText, Task task) {
        context.startActivity(getRejectedNotificationIntent(context, presetValidationText,
                validationText, task, true));
    }

    public static Intent getRejectedNotificationIntent(Context context, String presetValidationText,
                                                       String validationText, Task task,
                                                       boolean showLeftButton) {
        Spanned notificationText = Html.fromHtml(context.getString(R.string.reject_mission_notification_text,
                presetValidationText, validationText, task.getName(), task.getLocationName(), task.getAddress(), task.getId()));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.NOTIFICATION_TYPE_ID, NotificationActivity.NotificationType.mission_rejected.getId());
        intent.putExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, R.color.red);
        intent.putExtra(Keys.TITLE_ICON_RES_ID, R.drawable.info_icon);
        intent.putExtra(Keys.NOTIFICATION_TITLE, context.getString(R.string.reject_mission_notification_title));
        intent.putExtra(Keys.NOTIFICATION_TEXT, notificationText);
        intent.putExtra(Keys.RIGHT_BUTTON_RES_ID, R.string.ok);
        intent.putExtra(Keys.SHOW_LEFT_BUTTON, showLeftButton);
        return intent;
    }

    /**
     * Start default notification
     *
     * @param context - current context
     * @param title   - current title
     * @param message - current message
     * @param intent  - current intent
     * @return Boolean
     */
    public static Boolean generateNotification(Context context, String title, String message, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(Html.fromHtml(message).toString()));


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

    /**
     * Start overlay notification
     *
     * @param context     - current context
     * @param name        - current name
     * @param description - current description
     * @param intent      - current intent
     */

    public static void showOverlayNotification(final Context context, String name, String description,
                                               final Intent intent) {
        final CustomNotificationStatus notificationStatus = new CustomNotificationStatus();

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        final View mTopView = LayoutInflater.from(App.getInstance()).inflate(R.layout.view_custom_notification, null);

        TextView nameTextView = (TextView) mTopView.findViewById(R.id.name);
        TextView descriptionTextView = (TextView) mTopView.findViewById(R.id.description);
        nameTextView.setText(name);
        descriptionTextView.setText(description);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP;
        params.windowAnimations = R.style.CustomNotificationStyle;

        wm.addView(mTopView, params);

        mTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(mTopView);
                notificationStatus.setHiden(true);

                if (intent != null) {
                    context.startActivity(IntentUtils.getMainActivityIntent(App.getInstance()));
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!notificationStatus.isHiden()) {
                    wm.removeView(mTopView);
                }

            }
        }, 8000);
    }
}
