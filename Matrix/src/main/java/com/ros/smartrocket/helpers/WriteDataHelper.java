package com.ros.smartrocket.helpers;

import android.content.Context;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.fragment.SettingsFragment;
import com.ros.smartrocket.utils.PreferencesManager;

public class WriteDataHelper {
    //private static final String TAG = WriteDataHelper.class.getSimpleName();
    public WriteDataHelper() {

    }

    /**
     * 1. Clean preferences
     * 2. Remove Surveys, Tasks, Questions, Answers from DB
     */
    public static void prepareLogout(Context context) {
        PreferencesManager.getInstance().clearAll();
        SettingsFragment.setCurrentLanguage();

        SurveysBL.removeAllSurveysFromDB(context);
        TasksBL.removeAllTasksFromDB(context);
        QuestionsBL.removeAllQuestionsFromDB(context);
        AnswersBL.removeAllAnswers(context);
    }
}
