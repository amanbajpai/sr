package com.ros.smartrocket.utils.helpers;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMRegistrar;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.NotificationBL;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.net.TaskReminderService;
import com.ros.smartrocket.net.fcm.DeleteTokenService;
import com.ros.smartrocket.utils.LocaleUtils;
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
        preferencesManager.removeToken();
        preferencesManager.setTokenForUploadFile("");
        preferencesManager.setTokenUpdateDate(0);
        context.startService(new Intent(context, DeleteTokenService.class));
        clearDb(context);
        context.stopService(new Intent(context, TaskReminderService.class));
    }

    public static void prepareLogin(Context context, String currentEmail) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        String lastEmail = preferencesManager.getLastEmail();
        if (!lastEmail.equals(currentEmail)) {
            App.getInstance().setMyAccount(new MyAccount());
            int taskRadius = preferencesManager.getDefaultRadius();
            String lastPassword = preferencesManager.getLastPassword();
            String token = preferencesManager.getToken();
            String tokenForUpdateFile = preferencesManager.getTokenForUploadFile();
            long tokenUpdateDate = preferencesManager.getTokenUpdateDate();

            boolean useLocationServices = preferencesManager.getUseLocationServices();
            boolean useSocialSharing = preferencesManager.getUseSocialSharing();
            boolean useOnlyWiFiConnaction = preferencesManager.getUseOnlyWiFiConnaction();
            boolean useSaveImageToCameraRoll = preferencesManager.getUseSaveImageToCameraRoll();
            boolean usePushMessages = preferencesManager.getUsePushMessages();
            boolean useDeadlineReminder = preferencesManager.getUseDeadlineReminder();
            boolean isFirstLogin = preferencesManager.getIsFirstLogin();

            String languageCode = preferencesManager.getLanguageCode();
            long deadlineReminderMillisecond = preferencesManager.getDeadlineReminderMillisecond();
            int uploadTaskLimit = preferencesManager.get3GUploadTaskLimit();
            int uploadMonthLimit = preferencesManager.get3GUploadMonthLimit();

            preferencesManager.clearAll();

            preferencesManager.setDefaultRadius(taskRadius);
            preferencesManager.setLastEmail(currentEmail);
            preferencesManager.setLastPassword(lastPassword);
            preferencesManager.setToken(token);
            preferencesManager.setTokenForUploadFile(tokenForUpdateFile);
            preferencesManager.setTokenUpdateDate(tokenUpdateDate);

            preferencesManager.setUseLocationServices(useLocationServices);
            preferencesManager.setUseDeadlineReminder(useDeadlineReminder);
            preferencesManager.setUsePushMessages(usePushMessages);
            preferencesManager.setUseSocialSharing(useSocialSharing);
            preferencesManager.setUseSaveImageToCameraRoll(useSaveImageToCameraRoll);
            preferencesManager.setUseOnlyWiFiConnaction(useOnlyWiFiConnaction);
            preferencesManager.setIsFirstLogin(isFirstLogin);

            preferencesManager.setLanguageCode(languageCode);
            preferencesManager.setDeadlineReminderMillisecond(deadlineReminderMillisecond);
            preferencesManager.set3GUploadTaskLimit(uploadTaskLimit);
            preferencesManager.set3GUploadMonthLimit(uploadMonthLimit);

            LocaleUtils.setCurrentLanguage();
            clearDb(context);
        }

    }

    private static void clearDb(Context context) {
        WavesBL.removeAllWavesFromDB(context);
        TasksBL.removeAllTasksFromDB(context);
        QuestionsBL.removeAllQuestionsFromDB(context);
        AnswersBL.removeAllAnswers(context);
        NotificationBL.removeAllNotifications(context);
        GCMRegistrar.unregister(context);
    }
}
