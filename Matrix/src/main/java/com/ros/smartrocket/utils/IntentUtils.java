package com.ros.smartrocket.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.TextUtils;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.flow.login.activate.ActivateAccountActivity;
import com.ros.smartrocket.flow.cash.CashingOutActivity;
import com.ros.smartrocket.flow.cash.confirm.CashingOutConfirmationActivity;
import com.ros.smartrocket.flow.cash.CashingOutSuccessActivity;
import com.ros.smartrocket.ui.activity.FullScreenImageActivity;
import com.ros.smartrocket.ui.activity.FullScreenVideoActivity;
import com.ros.smartrocket.ui.activity.MainActivity;
import com.ros.smartrocket.ui.activity.MapActivity;
import com.ros.smartrocket.ui.activity.PushNotificationActivity;
import com.ros.smartrocket.ui.activity.QuestionsActivity;
import com.ros.smartrocket.ui.activity.QuitQuestionActivity;
import com.ros.smartrocket.flow.login.password.update.SetNewPasswordActivity;
import com.ros.smartrocket.flow.settings.SettingsActivity;
import com.ros.smartrocket.ui.activity.ShareActivity;
import com.ros.smartrocket.ui.activity.TaskDetailsActivity;
import com.ros.smartrocket.ui.activity.TaskValidationActivity;
import com.ros.smartrocket.ui.activity.WaveDetailsActivity;
import com.ros.smartrocket.flow.login.LoginActivity;
import com.ros.smartrocket.flow.login.password.forgot.ForgotPasswordSuccessActivity;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

/**
 * Utils class for easy work with UI Views
 */
public class IntentUtils {
    private static final String TAG = IntentUtils.class.getSimpleName();

    /**
     * Return intent for opening Questions screen
     *
     * @param context - context
     * @param taskId  - current taskId
     * @return Intent
     */
    public static Intent getQuestionsIntent(Context context, int taskId, int missionId) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);
        return intent;
    }

    /**
     * Return intent for opening Questions screen in preview mode
     *
     * @param context   - context
     * @param taskId    - current TaskId
     * @param missionId - current missionId
     * @return Intent for preview mode
     */
    public static Intent getPreviewQuestionsIntent(Context context, int taskId, int missionId) {
        Intent intent = getQuestionsIntent(context, taskId, missionId);
        intent.putExtra(Keys.KEY_IS_PREVIEW, true);
        return intent;
    }

    /**
     * Return intent for reopening Re-Do Questions screen
     * In this case API call won't be performed
     *
     * @param context - context
     * @param taskId  - current taskId
     * @return Intent
     */
    public static Intent getReCheckReDoQuestionsIntent(Context context, int taskId, int missionId) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);
        intent.putExtra(Keys.IS_REDO_REOPEN, true);
        return intent;
    }

    /**
     * Return intent for opening Task detail screen
     *
     * @param context - context
     * @param taskId  - current taskId
     * @return Intent
     */
    public static Intent getTaskDetailIntent(Context context, int taskId, int missionId, int statusId, boolean
            isPreClaim) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);
        intent.putExtra(Keys.STATUS_ID, statusId);
        intent.putExtra(Keys.IS_PRECLAIM, isPreClaim);
        return intent;
    }

    /**
     * Return intent for opening Login screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getLoginIntentForLogout(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Return intent for opening Login screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getLoginIntentForPushNotificationsActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.START_PUSH_NOTIFICATIONS_ACTIVITY, true);
        return intent;
    }

    /**
     * Return intent for opening Main screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getMainActivityIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    /**
     * Return intent for opening Task Validation screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getTaskValidationIntent(Context context, int taskId, int missionId, boolean firstlySelection,
                                                 boolean isRedo) {
        Intent intent = new Intent(context, TaskValidationActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);
        intent.putExtra(Keys.FIRSTLY_SELECTION, firstlySelection);
        intent.putExtra(Keys.IS_REDO, isRedo);
        return intent;
    }

    /**
     * Return intent for opening Quit Question screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getQuitQuestionIntent(Context context, Question question) {
        Intent intent = new Intent(context, QuitQuestionActivity.class);
        intent.putExtra(Keys.QUESTION, question);
        return intent;
    }

    /**
     * Return intent for opening Quit Question screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getWaveMapIntent(Context context, int waveId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.MAP_VIEW_ITEM_ID, waveId);
        bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.WAVE_TASKS.toString());

        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * Return intent for opening Forgot Password screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getForgotPasswordSuccessIntent(Context context, String email) {
        Intent intent = new Intent(context, ForgotPasswordSuccessActivity.class);
        intent.putExtra(Keys.EMAIL, email);
        return intent;
    }

    /**
     * Return intent for sending email
     *
     * @param email - send to email
     * @param text  - text to send
     * @return Intent
     */
    public static Intent getEmailIntent(String subject, String email, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setType("message/rfc822");
        if (!TextUtils.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }

        if (!TextUtils.isEmpty(email)) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        }

        if (!TextUtils.isEmpty(text)) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        return intent;
    }

    public static Intent getLogEmailIntent(String subject, String email, String text) {
        Intent intent = getEmailIntent(subject, email, text);
        File file = FileProcessingManager.getTempFile(FileProcessingManager.FileType.TEXT, FileProcessingManager.FILE_LOGS, true);
        if (file != null) {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        }
        return intent;
    }

    /**
     * Return intent for sending sms
     *
     * @return Intent
     */

    @TargetApi(19)
    public static Intent getSmsIntent(Context context, String phoneNumber, String text) {
        Intent intent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);

            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);

            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName);
            }

            if (!TextUtils.isEmpty(phoneNumber)) {
                try {
                    intent.setData(Uri.parse("sms:" + URLEncoder.encode(phoneNumber, "UTF-8")));
                } catch (Exception e) {
                    L.e(TAG, "GetSmsIntent SDK>=19 error: " + e.getMessage(), e);
                }
            }
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra("sms_body", text);

            if (!TextUtils.isEmpty(phoneNumber)) {
                try {
                    intent.setData(Uri.parse("sms:" + URLEncoder.encode(phoneNumber, "UTF-8")));
                } catch (Exception e) {
                    L.e(TAG, "GetSmsIntent SDK<19 error: " + e.getMessage(), e);
                }
            } else {
                intent.setData(Uri.parse("sms:"));
            }
        }

        return intent;
    }

    /**
     * Return intent for sharing through Facebook
     *
     * @return Intent
     */

    public static Intent getShareFacebookIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.facebook.katana");
        return intent;
    }

    /**
     * Return intent for sharing through Twitter
     *
     * @return Intent
     */
    public static Intent getShareTwitterIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.twitter.android");
        return intent;
    }

    /**
     * Return intent for sharing through LinkedIn
     *
     * @return Intent
     */
    public static Intent getShareLinkedInIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.linkedin.android");
        return intent;
    }

    /**
     * Return intent for sharing through WeChat
     *
     * @return Intent
     */
    public static Intent getShareWeChatIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.tencent.mm");
        return intent;
    }

    /**
     * Return intent for sharing through WhatsApp
     *
     * @return Intent
     */
    public static Intent getShareWhatsAppIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.whatsapp");
        return intent;
    }

    /**
     * Return intent for sharing through TencentWeibo
     *
     * @return Intent
     */
    public static Intent getShareTencentWeiboIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.tencent.WBlog");
        return intent;
    }

    /**
     * Return intent for sharing through SinaWeibo
     *
     * @return Intent
     */
    public static Intent getShareSinaWeiboIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.sina.weibo");
        return intent;
    }

    /**
     * Return intent for sharing through QZone
     *
     * @return Intent
     */
    public static Intent getShareQZoneIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.qzone");
        return intent;
    }

    /**
     * Return intent for sharing
     *
     * @return Intent
     */
    public static Intent getShareIntent(String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        if (!TextUtils.isEmpty(text)) {
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        return intent;
    }

    /**
     * Open market
     *
     * @return Intent
     */
    public static Intent getGooglePlayIntent(String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setData(Uri.parse("market://details?id=" + packageName));

        return intent;
    }

    /**
     * Open browser
     *
     * @param url - current url
     * @return Intent
     */
    public static Intent getBrowserIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }

    /**
     * Check if intent available
     *
     * @param context - current context
     * @param intent  - current intent
     * @return boolean
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        boolean isAvailable = false;
        try {
            final PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            isAvailable = !list.isEmpty();
        } catch (Exception e) {
            L.e(TAG, "IsIntentAvailable error: " + e.getMessage(), e);
        }
        return intent != null && isAvailable;
    }

    /**
     * Return intent for opening Wave detail screen
     *
     * @param context - current context
     * @return Intent
     */

    public static Intent getWaveDetailsIntent(Context context, int waveId, int statusId, boolean isPreClaim) {
        Intent intent = new Intent(context, WaveDetailsActivity.class);
        intent.putExtra(Keys.WAVE_ID, waveId);
        intent.putExtra(Keys.STATUS_ID, statusId);
        intent.putExtra(Keys.IS_PRECLAIM, isPreClaim);
        return intent;
    }

    /**
     * Return intent for opening activate account screen
     *
     * @param context - current context
     * @param email   - current email
     * @param token   - current token
     * @return Intent
     */

    public static Intent getActivateAccountIntent(Context context, String email, String token) {
        Intent intent = new Intent(context, ActivateAccountActivity.class);
        intent.putExtra(Keys.EMAIL, email);
        intent.putExtra(Keys.TOKEN, token);
        return intent;
    }

    /**
     * Return intent for opening inserting new password screen
     *
     * @param context - current context
     * @param email   - current email
     * @param token   - current token
     * @return Intent
     */

    public static Intent getSetNewPasswordIntent(Context context, String email, String token) {
        Intent intent = new Intent(context, SetNewPasswordActivity.class);
        intent.putExtra(Keys.EMAIL, email);
        intent.putExtra(Keys.TOKEN, token);
        return intent;
    }

    /**
     * Return intent for opening images in full size
     *
     * @param context        - current context
     * @param filePath       - file path to open
     * @param rotateFromExif - need to rotate by exif flag
     * @return Intent
     */

    public static Intent getFullScreenImageIntent(Context context, String filePath, boolean rotateFromExif) {
        Intent intent = new Intent(context, FullScreenImageActivity.class);
        intent.putExtra(Keys.BITMAP_FILE_PATH, filePath);
        intent.putExtra(Keys.ROTATE_BY_EXIF, rotateFromExif);
        return intent;
    }

    /**
     * Return intent for opening video in full size
     *
     * @param context  - current context
     * @param filePath - file path to open
     * @return Intent
     */

    public static Intent getFullScreenVideoIntent(Context context, String filePath) {
        Intent intent = new Intent(context, FullScreenVideoActivity.class);
        intent.putExtra(Keys.VIDEO_FILE_PATH, filePath);
        return intent;
    }

    /**
     * Return intent for opening Settings screen
     *
     * @return Intent
     */

    public static Intent getSettingIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    /**
     * Return intent for opening Share screen
     *
     * @return Intent
     */

    public static Intent getShareIntent(Context context) {
        return new Intent(context, ShareActivity.class);
    }

    /**
     * Send broadcast for refresh Main menu
     *
     * @param context - current context
     */
    public static void refreshProfileAndMainMenu(Context context) {
        Intent intent = new Intent(Keys.REFRESH_MAIN_MENU);
        context.sendBroadcast(intent);
    }

    /**
     * Send broadcast for refresh Main menu
     *
     * @param context - current context
     */
    public static void refreshMainMenuMyTaskCount(Context context) {
        Intent intent = new Intent(Keys.REFRESH_MAIN_MENU_MY_TASK_COUNT);
        context.sendBroadcast(intent);
    }

    /**
     * Send broadcast for refresh push notifications list
     *
     * @param context - current context
     */
    public static void refreshPushNotificationsList(Context context) {
        Intent intent = new Intent(Keys.REFRESH_PUSH_NOTIFICATION_LIST);
        context.sendBroadcast(intent);
    }

    /**
     * Return intent for opening Cash Out screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getCashOutIntent(Context context) {
        Intent intent = new Intent(context, CashingOutActivity.class);
        return intent;
    }

    /**
     * Return intent for opening Cash Out Confirmation screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getCashOutConfirmationIntent(Context context) {
        return new Intent(context, CashingOutConfirmationActivity.class);
    }

    /**
     * Return intent for opening Cash Out Success screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getCashOutSuccessIntent(Context context) {
        return new Intent(context, CashingOutSuccessActivity.class);
    }

    /**
     * Return intent for opening Notifications List screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getNotificationsIntent(Context context) {
        return new Intent(context, PushNotificationActivity.class);
    }
}
