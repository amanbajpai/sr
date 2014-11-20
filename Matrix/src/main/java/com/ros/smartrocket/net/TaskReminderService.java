package com.ros.smartrocket.net;

import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TaskReminderService extends Service {
    private static final String TAG = TaskReminderService.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private AsyncQueryHandler dbHandler;

    private Timer reminderTimer;

    public static final int COOKIE_DEADLINE_REMINDER = 1;
    public static final int COOKIE_EXPIRED_TASK = 2;

    public TaskReminderService() {
    }

    @Override
    public void onCreate() {
        L.i(TAG, "onCreate");

        dbHandler = new DbHandler(getContentResolver());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.i(TAG, "onStartCommand: startId=" + startId);

        if (intent != null) {
            String action = intent.getAction();
            L.i(TAG, "getAction: " + action);

            if (Keys.ACTION_START_REMINDER_TIMER.equals(action)) {
                startReminderTimer();
            } else if (Keys.ACTION_STOP_REMINDER_TIMER.equals(action)) {
                stopReminderTimer();
            }
        }
        return START_STICKY;
    }

    public void startReminderTimer() {
        if (reminderTimer != null) {
            L.i(TAG, "Restart reminderTimer");
            reminderTimer.cancel();
        } else {
            L.i(TAG, "Start reminderTimer");
        }

        new Thread() {
            public void run() {
                try {
                    reminderTimer = new Timer();
                    reminderTimer.schedule(new TimerTask() {
                        public void run() {
                            L.i(TAG, "In timer. Start ReminderTimer");
                            long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

                            if (preferencesManager.getUseDeadlineReminder()) {
                                long fromTime = currentTimeInMillis + preferencesManager.getDeadlineReminderMillisecond();
                                long tillTime = fromTime + Config.DEADLINE_REMINDER_MILLISECONDS;

                                TasksBL.getTaskToRemindFromDB(dbHandler, COOKIE_DEADLINE_REMINDER, fromTime, tillTime);
                            }

                            if (preferencesManager.getUsePushMessages()) {
                                long fromTime = currentTimeInMillis - Config.DEADLINE_REMINDER_MILLISECONDS;
                                long tillTime = currentTimeInMillis;

                                TasksBL.getTaskToRemindFromDB(dbHandler, COOKIE_EXPIRED_TASK, fromTime, tillTime);
                            }
                        }
                    }, 0, Config.DEADLINE_REMINDER_MILLISECONDS);

                } catch (Exception e) {
                    L.e(TAG, "StartReminderTimer error: " + e.getMessage(), e);
                }
            }
        }.start();
    }

    public void stopReminderTimer() {
        L.i(TAG, "Stop reminderTimer");
        if (reminderTimer != null) {
            reminderTimer.cancel();
        }
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, final Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    Task task = TasksBL.convertCursorToTaskOrNull(cursor);

                    L.i(TAG, "Is task found: " + (task != null));
                    if (task != null && App.getInstance() != null) {
                        int type = (Integer) cookie;
                        if (COOKIE_DEADLINE_REMINDER == type) {
                            L.i(TAG, "Show Deadline reminder dialog");

                            NotificationUtils.startDeadlineNotificationActivity(TaskReminderService.this,
                                    task.getLongExpireDateTime(),
                                    task.getWaveId(), task.getId(),
                                    task.getName(), task.getCountryName(), task.getAddress(), task.getStatusId());

                        } else if (COOKIE_EXPIRED_TASK == type) {
                            L.i(TAG, "Show Expire task dialog");
                            NotificationUtils.startExpiredNotificationActivity(TaskReminderService.this,
                                    task.getName(), task.getCountryName(), task.getAddress());
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        L.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        if (reminderTimer != null) {
            reminderTimer.cancel();
        }
        super.onDestroy();
    }
}
