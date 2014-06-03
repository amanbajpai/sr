package com.ros.smartrocket.helpers;

import android.content.Context;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.fragment.SettingsFragment;
import com.ros.smartrocket.utils.PreferencesManager;

public class WriteDataHelper {
    public WriteDataHelper() {

    }

    /**
     * 1. Clean preferences
     * 2. Remove Surveys, Tasks, Questions, Answers from DB
     */
    public static void prepareLogout(Context context) {
        PreferencesManager preferencesManager =  PreferencesManager.getInstance();
        int taskRadius = preferencesManager.getDefaultRadius();
        String lastEmail = preferencesManager.getLastEmail();
        String lastPassword = preferencesManager.getLastPassword();

        preferencesManager.clearAll();

        SettingsFragment.setCurrentLanguage();
        preferencesManager.setDefaultRadius(taskRadius);
        preferencesManager.setLastEmail(lastEmail);
        preferencesManager.setLastPassword(lastPassword);

        SurveysBL.removeAllSurveysFromDB(context);
        TasksBL.removeAllTasksFromDB(context);
        QuestionsBL.removeAllQuestionsFromDB(context);
        AnswersBL.removeAllAnswers(context);
    }
}
