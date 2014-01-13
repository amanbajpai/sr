package com.ros.smartrocket.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.activity.LoginActivity;
import com.ros.smartrocket.activity.QuestionsActivity;
import com.ros.smartrocket.activity.TaskDetailsActivity;
import com.ros.smartrocket.activity.TaskValidationActivity;

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
}
