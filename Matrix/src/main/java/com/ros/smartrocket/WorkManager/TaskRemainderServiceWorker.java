package com.ros.smartrocket.WorkManager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskRemainderServiceWorker extends Worker {

    private static final String TAG = TaskRemainderServiceWorker.class.getSimpleName();
    public static final int PERIOD = 60;
    private Disposable reminderDisposable;
    private CompositeDisposable compositeDisposable;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();


    private Context mContext;
    private Data outputData;


    public TaskRemainderServiceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        mContext = getApplicationContext();

        String inputKey = getInputData().getString(Keys.GET_KEY_FROM_WORKER);

        if (Keys.ACTION_START_REMINDER_TIMER.equals(inputKey)) {
            System.out.println("taskDesc = " + inputKey);
            Log.e(TAG, "Working in BackGround");
            startReminderTimer();
        }else if (Keys.ACTION_STOP_REMINDER_TIMER.equals(inputKey)) {
            stopReminderTimer();
        }

        outputData = new Data.Builder()
                .putString(Keys.GET_KEY_FROM_WORKER, inputKey)
                .build();


        return Result.success(outputData);
    }


    public void startReminderTimer() {
        if (reminderDisposable != null) {
            reminderDisposable.dispose();
        }
        reminderDisposable =
                Observable.interval(0, PERIOD, TimeUnit.SECONDS, Schedulers.io())
                        .subscribe(__ -> getTaskToRemindFromDB(), this::onError);
    }

    private void getTaskToRemindFromDB() {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        if (preferencesManager.getUsePushMessages()) {
            long fromTime = currentTimeInMillis - Config.DEADLINE_REMINDER_MILLISECONDS;
            getExpiredTaskToRemindFromDB(fromTime, currentTimeInMillis);
        }

        if (preferencesManager.getUseDeadlineReminder()) {
            long fromTime = currentTimeInMillis + preferencesManager.getDeadlineReminderMillisecond();
            long tillTime = fromTime + Config.DEADLINE_REMINDER_MILLISECONDS;
            getDeadlineTaskToRemindFromDB(fromTime, tillTime);
        }
    }

    private void getExpiredTaskToRemindFromDB(long fromTime, long tillTime) {
        addDisposable(
                TasksBL.taskToRemindObservable(fromTime, tillTime)
                        .observeOn(Schedulers.computation())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::expiredTaskToRemindLoaded, this::onError));
    }

    private void expiredTaskToRemindLoaded(Task task) {
        if (task.getId() != null && App.getInstance() != null) {
            TasksBL.removeTask(task.getId());
            NotificationUtils.startExpiredNotificationActivity(mContext, task);
        }
    }

    private void getDeadlineTaskToRemindFromDB(long fromTime, long tillTime) {
        addDisposable(TasksBL.taskToRemindObservable(fromTime, tillTime)
                .observeOn(Schedulers.computation())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this::deadlineTaskToRemindLoaded, this::onError));
    }

    private void deadlineTaskToRemindLoaded(Task task) {
        if (task.getId() != null && App.getInstance() != null)
            NotificationUtils
                    .startDeadlineNotificationActivity(
                            mContext,
                            task.getLongExpireDateTime(),
                            task.getWaveId(), task.getId(), task.getMissionId(),
                            task.getName(), task.getCountryName(), task.getAddress(), task.getStatusId());
    }

    public void stopReminderTimer() {
        if (reminderDisposable != null) reminderDisposable.dispose();
    }


    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    private void unDispose() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void onError(Throwable t) {
        Log.e(TAG, "Error on TaskRemainderServiceWorker", t);
    }

}