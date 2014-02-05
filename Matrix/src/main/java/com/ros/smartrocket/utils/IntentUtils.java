package com.ros.smartrocket.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.text.TextUtils;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.activity.LoginActivity;
import com.ros.smartrocket.activity.QuestionsActivity;
import com.ros.smartrocket.activity.SurveyDetailsActivity;
import com.ros.smartrocket.activity.TaskDetailsActivity;
import com.ros.smartrocket.activity.TaskValidationActivity;
import com.ros.smartrocket.db.entity.Survey;

import java.net.URLEncoder;
import java.util.List;

/**
 * Utils class for easy work with UI Views
 */
public class IntentUtils {
    //private static final String TAG = "IntentUtils";

    /**
     * Return intent for opening Questions screen
     *
     * @param context
     * @param surveyId
     * @param taskId
     * @return
     */
    public static Intent getQuestionsIntent(Context context, int surveyId, int taskId) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(Keys.SURVEY_ID, surveyId);
        intent.putExtra(Keys.TASK_ID, taskId);
        return intent;
    }

    /**
     * Return intent for opening Task detail screen
     *
     * @param context
     * @param taskId
     * @return
     */
    public static Intent getTaskDetailIntent(Context context, int taskId) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        return intent;
    }

    /**
     * Return intent for opening Login screen
     *
     * @param context
     * @return
     */
    public static Intent getLoginIntentForLogout(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    /**
     * Return intent for opening Task Validation screen
     *
     * @param context
     * @return
     */
    public static Intent getTaskValidationIntent(Context context, int taskId) {
        Intent intent = new Intent(context, TaskValidationActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        return intent;
    }

    /**
     * Return intent for sending email
     *
     * @param email
     * @param text
     * @return
     */
    public static Intent getEmailIntent(String email, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setType("message/rfc822");
        if (!TextUtils.isEmpty(email)) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        }

        if (!TextUtils.isEmpty(text)) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        return intent;
    }

    /**
     * Return intent for sending sms
     *
     * @return
     */
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
                    e.printStackTrace();
                }
            }
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra("sms_body", text);

            if (!TextUtils.isEmpty(phoneNumber)) {
                try {
                    intent.setData(Uri.parse("sms:" + URLEncoder.encode(phoneNumber, "UTF-8")));
                } catch (Exception e) {
                    e.printStackTrace();
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

    public static Intent getShareIntent(String subject, String text) {
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

    public static boolean isIntentAvailable(Context context, Intent intent) {
        boolean isAvailable = false;
        try {
            final PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            isAvailable = list.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent != null && isAvailable;
    }

    /**
     * Return intent for opening Survey detail screen
     *
     * @param context
     * @return
     */

    public static Intent getSurveyDetailsIntent(Context context, Survey survey) {
        Intent intent = new Intent(context, SurveyDetailsActivity.class);
        intent.putExtra(Keys.SURVEY, survey);
        return intent;
    }
}
