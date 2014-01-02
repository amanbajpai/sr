package com.ros.smartrocket.utils;

import android.content.Context;
import android.content.Intent;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.activity.LoginActivity;
import com.ros.smartrocket.activity.QuestionsActivity;
import com.ros.smartrocket.activity.TaskDetailsActivity;
import com.ros.smartrocket.activity.TaskValidationActivity;

/**
 * Utils class for easy work with UI Views
 */
public class IntentUtils {
    private static final String TAG = "IntentUtils";

    /**
     * Show simple Toast message
     *
     * @param context
     * @param survetId
     */

    public static Intent getQuestionsIntent(Context context, int survetId, int taskId) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(Keys.SURVEY_ID, survetId);
        intent.putExtra(Keys.TASK_ID, taskId);
        return intent;
    }

    public static Intent getTaskDetailIntent(Context context, int taskId) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        return intent;
    }

    public static Intent getLoginIntentForLogout(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public static Intent getTaskValidationIntent(Context context, int taskId) {
        Intent intent = new Intent(context, TaskValidationActivity.class);
        intent.putExtra(Keys.TASK_ID, taskId);
        return intent;
    }
}
