package com.ros.smartrocket.WorkManager;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.ros.smartrocket.Keys;

public class WorkManagerScheduler {

    private static OneTimeWorkRequest mWorkRequest;


    public static void callWorkManager(String actionCheckNotUploadedFiles) {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putString(Keys.GET_KEY_FROM_WORKER, actionCheckNotUploadedFiles)
                .build();

        if (actionCheckNotUploadedFiles.equals(Keys.ACTION_START_REMINDER_TIMER)) {

            mWorkRequest = new OneTimeWorkRequest.Builder(TaskRemainderServiceWorker.class)
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance().enqueueUniqueWork("task_reminder_worker", ExistingWorkPolicy.KEEP, mWorkRequest);

        } else if (actionCheckNotUploadedFiles.equals(Keys.ACTION_STOP_REMINDER_TIMER)) {

            mWorkRequest = new OneTimeWorkRequest.Builder(TaskRemainderServiceWorker.class)
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance().enqueueUniqueWork("task_reminder_worker", ExistingWorkPolicy.KEEP, mWorkRequest);
        } else {
            mWorkRequest = new OneTimeWorkRequest.Builder(UploadFileServiceWorker.class)
                    .setInputData(data)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance().enqueueUniqueWork("upload_file_worker", ExistingWorkPolicy.KEEP, mWorkRequest);
        }

    }
}
