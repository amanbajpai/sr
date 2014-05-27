package com.ros.smartrocket.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.text.TextUtils;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.activity.ActivateAccountActivity;
import com.ros.smartrocket.activity.ForgotPasswordSuccessActivity;
import com.ros.smartrocket.activity.LoginActivity;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.activity.QuestionsActivity;
import com.ros.smartrocket.activity.QuitQuestionActivity;
import com.ros.smartrocket.activity.SurveyDetailsActivity;
import com.ros.smartrocket.activity.TaskDetailsActivity;
import com.ros.smartrocket.activity.TaskValidationActivity;
import com.ros.smartrocket.activity.TermsAndConditionActivity;
import com.ros.smartrocket.db.entity.Question;
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
     * @param context - context
     * @param surveyId - current surveyId
     * @param taskId - current taskId
     * @return Intent
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
     * @param context - context
     * @param taskId - current taskId
     * @return Intent
     */
    public static Intent getTaskDetailIntent(Context context, int taskId) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    public static Intent getTaskValidationIntent(Context context, int taskId, boolean showRecheckAnswerButton) {
        Intent intent = new Intent(context, TaskValidationActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        intent.putExtra(Keys.SHOW_RECHECK_ANSWERS_BUTTON, showRecheckAnswerButton);
        return intent;
    }

    /**
     * Return intent for opening Terms And Condition screen
     *
     * @param context - context
     * @return Intent
     */
    public static Intent getTermsAndConditionIntent(Context context, int versionId) {
        Intent intent = new Intent(context, TermsAndConditionActivity.class);
        intent.putExtra(Keys.T_AND_C_VERSION, versionId);
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
     * Return intent for opening EnterGroupCodeActivity screen
     *
     * @param context - context
     * @param countryId - current countryId
     * @param cityId - current cityId
     * @param countryName - current countryName
     * @param cityName - current cityName
     * @param latitude - current latitude
     * @param longitude - current longitude
     * @return Intent
     *//*
    public static Intent getEnterGroupCodeIntent(Context context, int countryId, int cityId, String countryName,
                                                 String cityName, Double latitude, Double longitude) {
        Intent intent = new Intent(context, EnterGroupCodeActivity.class);
        intent.putExtra(Keys.COUNTRY_ID, countryId);
        intent.putExtra(Keys.COUNTRY_NAME, countryName);
        intent.putExtra(Keys.CITY_ID, cityId);
        intent.putExtra(Keys.CITY_NAME, cityName);
        intent.putExtra(Keys.LATITUDE, latitude);
        intent.putExtra(Keys.LONGITUDE, longitude);

        return intent;
    }*/

    /**
     * Return intent for sending email
     *
     * @param email
     * @param text
     * @return
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

    /**
     * Return intent for sending sms
     *
     * @return
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

    /**
     * Return intent for opening Survey detail screen
     *
     * @param context
     * @return
     */

    public static Intent getActivateAccountIntent(Context context) {
        Intent intent = new Intent(context, ActivateAccountActivity.class);
        return intent;
    }
}
