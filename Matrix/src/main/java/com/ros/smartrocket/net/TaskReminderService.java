package com.ros.smartrocket.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskReminderService extends Service {
    private static final String TAG = TaskReminderService.class.getSimpleName();
    public static final int PERIOD = 60;
    private Disposable reminderDisposable;
    private CompositeDisposable compositeDisposable;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (Keys.ACTION_START_REMINDER_TIMER.equals(action)) {
                startReminderTimer();
            } else if (Keys.ACTION_STOP_REMINDER_TIMER.equals(action)) {
                stopReminderTimer();
            }
        }
        return START_STICKY;
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
            NotificationUtils.startExpiredNotificationActivity(TaskReminderService.this, task);
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
                            TaskReminderService.this,
                            task.getLongExpireDateTime(),
                            task.getWaveId(), task.getId(), task.getMissionId(),
                            task.getName(), task.getCountryName(), task.getAddress(), task.getStatusId());
    }

    public void stopReminderTimer() {
        if (reminderDisposable != null) reminderDisposable.dispose();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopReminderTimer();
        unDispose();

        super.onDestroy();
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
        Log.e(TAG, "Error on TaskReminderService", t);
    }
}
