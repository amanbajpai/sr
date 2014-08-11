package com.ros.smartrocket.helpers;

import android.content.Context;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.fragment.SettingsFragment;
import com.ros.smartrocket.utils.PreferencesManager;

public class WriteDataHelper {
    public WriteDataHelper() {

    }

    /**
     * 1. Clean preferences
     * 2. Remove Waves, Tasks, Questions, Answers from DB
     */
    public static void prepareLogout(Context context) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        int taskRadius = preferencesManager.getDefaultRadius();
        String lastEmail = preferencesManager.getLastEmail();
        String lastPassword = preferencesManager.getLastPassword();

        boolean useLocationServices = preferencesManager.getUseLocationServices();
        boolean useSocialSharing = preferencesManager.getUseSocialSharing();
        boolean useOnlyWiFiConnaction = preferencesManager.getUseOnlyWiFiConnaction();
        boolean useSaveImageToCameraRoll = preferencesManager.getUseSaveImageToCameraRoll();
        boolean usePushMessages = preferencesManager.getUsePushMessages();
        boolean useDeadlineReminder = preferencesManager.getUseDeadlineReminder();

        String languageCode = preferencesManager.getLanguageCode();
        long deadlineReminderMillisecond = preferencesManager.getDeadlineReminderMillisecond();
        int uploadTaskLimit = preferencesManager.get3GUploadTaskLimit();
        int uploadMonthLimit = preferencesManager.get3GUploadMonthLimit();

        preferencesManager.clearAll();

        SettingsFragment.setCurrentLanguage();
        preferencesManager.setDefaultRadius(taskRadius);
        preferencesManager.setLastEmail(lastEmail);
        preferencesManager.setLastPassword(lastPassword);

        preferencesManager.setUseLocationServices(useLocationServices);
        preferencesManager.setUseDeadlineReminder(useDeadlineReminder);
        preferencesManager.setUsePushMessages(usePushMessages);
        preferencesManager.setUseSocialSharing(useSocialSharing);
        preferencesManager.setUseSaveImageToCameraRoll(useSaveImageToCameraRoll);
        preferencesManager.setUseOnlyWiFiConnaction(useOnlyWiFiConnaction);

        preferencesManager.setLanguageCode(languageCode);
        preferencesManager.setDeadlineReminderMillisecond(deadlineReminderMillisecond);
        preferencesManager.set3GUploadTaskLimit(uploadTaskLimit);
        preferencesManager.set3GUploadMonthLimit(uploadMonthLimit);

        WavesBL.removeAllWavesFromDB(context);
        TasksBL.removeAllTasksFromDB(context);
        QuestionsBL.removeAllQuestionsFromDB(context);
        AnswersBL.removeAllAnswers(context);
    }
}
