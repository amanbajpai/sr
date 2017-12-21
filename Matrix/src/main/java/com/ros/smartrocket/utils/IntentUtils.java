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
import com.ros.smartrocket.presentation.account.MyAccountActivity;
import com.ros.smartrocket.presentation.cash.CashingOutActivity;
import com.ros.smartrocket.presentation.cash.CashingOutSuccessActivity;
import com.ros.smartrocket.presentation.cash.confirm.CashingOutConfirmationActivity;
import com.ros.smartrocket.presentation.details.task.TaskDetailsActivity;
import com.ros.smartrocket.presentation.details.wave.WaveDetailsActivity;
import com.ros.smartrocket.presentation.login.LoginActivity;
import com.ros.smartrocket.presentation.login.activate.ActivateAccountActivity;
import com.ros.smartrocket.presentation.login.password.forgot.ForgotPasswordSuccessActivity;
import com.ros.smartrocket.presentation.login.password.update.SetNewPasswordActivity;
import com.ros.smartrocket.presentation.main.MainActivity;
import com.ros.smartrocket.presentation.map.MapActivity;
import com.ros.smartrocket.presentation.media.FullScreenImageActivity;
import com.ros.smartrocket.presentation.media.FullScreenVideoActivity;
import com.ros.smartrocket.presentation.notification.PushNotificationActivity;
import com.ros.smartrocket.presentation.question.main.QuestionsActivity;
import com.ros.smartrocket.presentation.settings.SettingsActivity;
import com.ros.smartrocket.presentation.share.ShareActivity;
import com.ros.smartrocket.presentation.validation.TaskValidationActivity;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;


public class IntentUtils {
    private static final String TAG = IntentUtils.class.getSimpleName();

    public static Intent getQuestionsIntent(Context context, int taskId, int missionId) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);
        return intent;
    }

    public static Intent getPreviewQuestionsIntent(Context context, int taskId, int missionId) {
        Intent intent = getQuestionsIntent(context, taskId, missionId);
        intent.putExtra(Keys.KEY_IS_PREVIEW, true);
        return intent;
    }

    public static Intent getReCheckReDoQuestionsIntent(Context context, int taskId, int missionId) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);
        intent.putExtra(Keys.IS_REDO_REOPEN, true);
        return intent;
    }

    public static Intent getTaskDetailIntent(Context context, int taskId, int missionId, int statusId, boolean
            isPreClaim) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);
        intent.putExtra(Keys.STATUS_ID, statusId);
        intent.putExtra(Keys.IS_PRECLAIM, isPreClaim);
        return intent;
    }

    public static Intent getLoginIntentForLogout(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getLoginIntentForPushNotificationsActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.START_PUSH_NOTIFICATIONS_ACTIVITY, true);
        return intent;
    }

    public static Intent getMainActivityIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public static Intent getTaskValidationIntent(Context context, int taskId, int missionId, boolean firstlySelection,
                                                 boolean isRedo) {
        Intent intent = new Intent(context, TaskValidationActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.MISSION_ID, missionId);
        intent.putExtra(Keys.FIRSTLY_SELECTION, firstlySelection);
        intent.putExtra(Keys.IS_REDO, isRedo);
        return intent;
    }

    public static Intent getWaveMapIntent(Context context, int waveId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.MAP_VIEW_ITEM_ID, waveId);
        bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.WAVE_TASKS.toString());

        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent getForgotPasswordSuccessIntent(Context context, String email) {
        Intent intent = new Intent(context, ForgotPasswordSuccessActivity.class);
        intent.putExtra(Keys.EMAIL, email);
        return intent;
    }

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
            intent.putExtra(Intent.EXTRA_STREAM, FileProcessingManager.getUriFromFile(file));
        }
        return intent;
    }

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

    public static Intent getShareFacebookIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.facebook.katana");
        return intent;
    }

    public static Intent getShareTwitterIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.twitter.android");
        return intent;
    }

    public static Intent getShareLinkedInIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.linkedin.android");
        return intent;
    }

    public static Intent getShareWeChatIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.tencent.mm");
        return intent;
    }

    public static Intent getShareWhatsAppIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.whatsapp");
        return intent;
    }

    public static Intent getShareTencentWeiboIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.tencent.WBlog");
        return intent;
    }

    public static Intent getShareSinaWeiboIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.sina.weibo");
        return intent;
    }

    public static Intent getShareQZoneIntent(String subject, String text) {
        Intent intent = getShareIntent(subject, text);
        intent.setPackage("com.qzone");
        return intent;
    }

    private static Intent getShareIntent(String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        if (!TextUtils.isEmpty(text)) {
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        return intent;
    }

    public static Intent getGooglePlayIntent(String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setData(Uri.parse("market://details?id=" + packageName));

        return intent;
    }

    public static Intent getBrowserIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }

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

    public static Intent getWaveDetailsIntent(Context context, int waveId, int statusId, boolean isPreClaim) {
        Intent intent = new Intent(context, WaveDetailsActivity.class);
        intent.putExtra(Keys.WAVE_ID, waveId);
        intent.putExtra(Keys.STATUS_ID, statusId);
        intent.putExtra(Keys.IS_PRECLAIM, isPreClaim);
        return intent;
    }

    public static Intent getActivateAccountIntent(Context context, String email, String token) {
        Intent intent = new Intent(context, ActivateAccountActivity.class);
        intent.putExtra(Keys.EMAIL, email);
        intent.putExtra(Keys.TOKEN, token);
        return intent;
    }

    public static Intent getSetNewPasswordIntent(Context context, String email, String token) {
        Intent intent = new Intent(context, SetNewPasswordActivity.class);
        intent.putExtra(Keys.EMAIL, email);
        intent.putExtra(Keys.TOKEN, token);
        return intent;
    }

    public static Intent getFullScreenImageIntent(Context context, String filePath) {
        Intent intent = new Intent(context, FullScreenImageActivity.class);
        intent.putExtra(Keys.BITMAP_FILE_PATH, filePath);
        return intent;
    }

    public static Intent getFullScreenVideoIntent(Context context, String filePath) {
        Intent intent = new Intent(context, FullScreenVideoActivity.class);
        intent.putExtra(Keys.VIDEO_FILE_PATH, filePath);
        return intent;
    }

    public static Intent getSettingIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    public static Intent getShareIntent(Context context) {
        return new Intent(context, ShareActivity.class);
    }

    public static void refreshProfileAndMainMenu(Context context) {
        Intent intent = new Intent(Keys.REFRESH_MAIN_MENU);
        context.sendBroadcast(intent);
    }

    public static void refreshMainMenuMyTaskCount(Context context) {
        Intent intent = new Intent(Keys.REFRESH_MAIN_MENU_MY_TASK_COUNT);
        context.sendBroadcast(intent);
    }

    public static void refreshPushNotificationsList(Context context) {
        Intent intent = new Intent(Keys.REFRESH_PUSH_NOTIFICATION_LIST);
        context.sendBroadcast(intent);
    }

    public static Intent getCashOutIntent(Context context) {
        Intent intent = new Intent(context, CashingOutActivity.class);
        return intent;
    }

    public static Intent getCashOutConfirmationIntent(Context context) {
        return new Intent(context, CashingOutConfirmationActivity.class);
    }

    public static Intent getCashOutSuccessIntent(Context context) {
        return new Intent(context, CashingOutSuccessActivity.class);
    }

    public static Intent getNotificationsIntent(Context context) {
        return new Intent(context, PushNotificationActivity.class);
    }

    public static Intent getMyAccountIntent(Context context) {
        return new Intent(context, MyAccountActivity.class);
    }
}
