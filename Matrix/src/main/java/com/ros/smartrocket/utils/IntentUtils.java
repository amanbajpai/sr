package com.ros.smartrocket.utils;

import android.content.Context;
import android.content.Intent;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.activity.QuestionsActivity;

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

    public static Intent getQuestionsIntent(Context context, int survetId) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(Keys.SURVEY_ID, survetId);
        return intent;
    }
}
