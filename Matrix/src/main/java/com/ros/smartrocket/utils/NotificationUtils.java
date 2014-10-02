package com.ros.smartrocket.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.activity.NotificationActivity;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.CustomNotificationStatus;
import org.json.JSONObject;

import java.util.Iterator;

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
            JSONObject messageObject = new JSONObject(jsonObject);
            int statusType = messageObject.optInt("StatusType");
            int waveId = messageObject.optInt("WaveId");
            int taskId = messageObject.optInt("TaskId");

            String taskName = getStringForCurrentLang(messageObject.optJSONObject("TaskName"));

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

            String locationName = messageObject.optString("LocationName");
            String missionAddress = messageObject.optString("MissionAddress");
            //String endDateTime = messageObject.optString("endDateTime");

            switch (TasksBL.getTaskStatusType(statusType)) {
                case RE_DO_TASK:
                    NotificationUtils.startRedoNotificationActivity(context, presetValidationText, validationText,
                            waveId, taskId, taskName, locationName, missionAddress);
                    break;
                case VALIDATED:
                    NotificationUtils.startApprovedNotificationActivity(context, presetValidationText, validationText,
                            taskName, locationName, missionAddress);
                    break;
                case REJECTED:
                    NotificationUtils.startRejectNotificationActivity(context, presetValidationText, validationText,
                            taskName, locationName,
                            missionAddress);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            L.e(TAG, "ShowTaskStatusChangedNotification error" + e.getMessage(), e);
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
                Iterator<String> iter = stringJsonObject.keys();
                resultString = stringJsonObject.optString(iter.next());
            }
        } catch (Exception e) {
            L.e(TAG, "Parse object Error: " + e.getMessage(), e);
        }

        return resultString;
    }

    /**
     * Start popup-notification about redo task
     *
     * @param context        - current context
     * @param waveId         - current waveId
     * @param taskId         - current taskId
     * @param missionName    - current missionName
     * @param locationName   - current locationName
     * @param missionAddress - current missionAddress
     */
    public static void startRedoNotificationActivity(Context context, String presetValidationText,
                                                     String validationText, int waveId, int taskId, String missionName,
                                                     String locationName, String missionAddress) {

        Spanned notificationText = Html.fromHtml(context.getString(R.string.redo_mission_notification_text,
                presetValidationText, validationText, missionName, locationName, missionAddress));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.WAVE_ID, waveId);
        intent.putExtra(Keys.TASK_ID, taskId);

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
     * @param missionName    - current missionName
     * @param locationName   - current locationName
     * @param missionAddress - current missionAddress
     */
    public static void startApprovedNotificationActivity(Context context, String presetValidationText,
                                                         String validationText, String missionName,
                                                         String locationName, String missionAddress) {

        Spanned notificationText = Html.fromHtml(context.getString(R.string.approved_mission_notification_text,
                presetValidationText, validationText, missionName, locationName, missionAddress));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.NOTIFICATION_TYPE_ID, NotificationActivity.NotificationType.mission_approved.getId());
        intent.putExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, R.color.green);
        intent.putExtra(Keys.TITLE_ICON_RES_ID, R.drawable.confirm_icon);
        intent.putExtra(Keys.NOTIFICATION_TITLE, context.getString(R.string.approved_mission_notification_title));
        intent.putExtra(Keys.NOTIFICATION_TEXT, notificationText);
        intent.putExtra(Keys.RIGHT_BUTTON_RES_ID, R.string.ok);

        context.startActivity(intent);
    }

    /**
     * Start popup-notification about reject task
     *
     * @param context        - current context
     * @param validationText - current validationText
     * @param missionName    - current missionName
     * @param locationName   - current locationName
     * @param missionAddress - current missionAddress
     */
    public static void startRejectNotificationActivity(Context context, String presetValidationText,
                                                       String validationText, String missionName,
                                                       String locationName, String missionAddress) {

        Spanned notificationText = Html.fromHtml(context.getString(R.string.reject_mission_notification_text,
                presetValidationText, validationText, missionName, locationName, missionAddress));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.NOTIFICATION_TYPE_ID, NotificationActivity.NotificationType.mission_rejected.getId());
        intent.putExtra(Keys.TITLE_BACKGROUND_COLOR_RES_ID, R.color.red);
        intent.putExtra(Keys.TITLE_ICON_RES_ID, R.drawable.info_icon);
        intent.putExtra(Keys.NOTIFICATION_TITLE, context.getString(R.string.reject_mission_notification_title));
        intent.putExtra(Keys.NOTIFICATION_TEXT, notificationText);
        intent.putExtra(Keys.RIGHT_BUTTON_RES_ID, R.string.ok);

        context.startActivity(intent);
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
                                                         String missionName,
                                                         String locationName, String missionAddress, int taskStatusId) {
        String deadlineDateText = UIUtils.longToString(deadlineTime, 3);

        Spanned notificationText = Html.fromHtml(context.getString(R.string.deadline_mission_notification_text,
                deadlineDateText, missionName, locationName, missionAddress));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(Keys.WAVE_ID, waveId);
        intent.putExtra(Keys.TASK_ID, taskId);

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
