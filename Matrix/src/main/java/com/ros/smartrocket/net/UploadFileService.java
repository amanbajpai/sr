package com.ros.smartrocket.net;

import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * For upload file:
 * 1. Save file uri to NotUploadedFile table
 * 2. Start service with tag ACTION_CHECK_NOT_UPLOADED_FILES
 * startService(new Intent(this, UploadFileService.class).setAction(Keys.ACTION_CHECK_NOT_UPLOADED_FILES));
 */

public class UploadFileService extends Service implements NetworkOperationListenerInterface {
    final String TAG = "UploadFileService";
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private ArrayList<NetworkOperationListenerInterface> networkOperationListeners = new ArrayList<NetworkOperationListenerInterface>();
    private AsyncQueryHandler dbHandler;
    private BroadcastReceiver receiver;

    private Timer uploadFilesTimer;
    private Timer showNotificationTimer;

    private static final String COOKIE_UPLOAD_FILE = "upload_file";
    private static final String COOKIE_SHOW_NOTIFICATION = "show_notification";

    private static final int MINUTE_IN_MILLISECONDS_15 = 1000 * 60 * 15;
    private static final int MINUTE_IN_MILLISECONDS_30 = 1000 * 60 * 30;
    private static final int MINUTE_IN_MILLISECONDS_60 = 1000 * 60 * 60;

    @Override
    public void onCreate() {
        L.i(TAG, "onCreate");

        dbHandler = new DbHandler(getContentResolver());
        receiver = new NetworkBroadcastReceiver();
        IntentFilter filter = new IntentFilter(NetworkService.BROADCAST_ACTION);

        addNetworkOperationListener(this);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);

        startService(new Intent(this, UploadFileService.class).setAction(Keys.ACTION_CHECK_NOT_UPLOADED_FILES));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.i(TAG, "onStartCommand: startId=" + startId);

        if (intent != null) {
            String action = intent.getAction();
            L.i(TAG, "getAction: " + action);

            if (Keys.ACTION_CHECK_NOT_UPLOADED_FILES.equals(action)) {
                startCheckNotUploadedFilesTimer();
            }
        }
        return START_STICKY;
    }

    public void startCheckNotUploadedFilesTimer() {
        if (uploadFilesTimer != null) {
            L.i(TAG, "Restart uploadFilesTimer");
            uploadFilesTimer.cancel();
        } else {
            L.i(TAG, "Start uploadFilesTimer");
        }

        if (showNotificationTimer != null) {
            L.i(TAG, "Restart showNotificationTimer");
            showNotificationTimer.cancel();
        } else {
            L.i(TAG, "Start showNotificationTimer");
        }

        new Thread() {
            public void run() {
                try {
                    uploadFilesTimer = new Timer();
                    uploadFilesTimer.schedule(new TimerTask() {
                        public void run() {

                            L.i(TAG, "In timer. Start");
                            if (canUploadNextFile(UploadFileService.this)) {
                                L.i(TAG, "Can upload file");
                                FilesBL.getFirstNotUploadedFileFromDB(dbHandler, 0,
                                        UIUtils.is3G(UploadFileService.this), COOKIE_UPLOAD_FILE);

                            }

                        }
                    }, 5000, Config.CHECK_NOT_UPLOADED_FILE_MILLISECONDS);

                    showNotificationTimer = new Timer();
                    showNotificationTimer.schedule(new TimerTask() {
                        public void run() {

                            L.i(TAG, "In timer. Start showNotificationTimer");
                            FilesBL.getNotUploadedFilesFromDB(dbHandler, COOKIE_SHOW_NOTIFICATION);

                        }
                    }, 5000, Config.SHOW_NOTIFICATION_FOR_NOT_UPLOADED_FILE_MILLISECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void stopUploadedFilesTimer() {
        L.i(TAG, "Stop uploadFilesTimer");
        if (uploadFilesTimer != null) {
            uploadFilesTimer.cancel();
        }
    }

    public void stopShowNotifiationTimer() {
        L.i(TAG, "Stop showNotificationTimer");
        if (showNotificationTimer != null) {
            showNotificationTimer.cancel();
        }
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, final Cursor cursor) {
            switch (token) {
                case NotUploadedFileDbSchema.Query.TOKEN_QUERY:
                    if (cookie.equals(COOKIE_UPLOAD_FILE)) {
                        NotUploadedFile notUploadedFile = FilesBL.convertCursorToNotUploadedFile(cursor);

                        if (notUploadedFile != null) {
                            //if (Calendar.getInstance().getTimeInMillis() <= notUploadedFile.getEndDateTime()) {
                            apiFacade.sendFile(UploadFileService.this, notUploadedFile);
                            /*} else {
                                FilesBL.deleteNotUploadedFileFromDbById(notUploadedFile.getId());
                            }*/
                        } else {
                            stopUploadedFilesTimer();
                        }
                    } else if (cookie.equals(COOKIE_SHOW_NOTIFICATION)) {
                        ArrayList<NotUploadedFile> notUploadedFileList = FilesBL.convertCursorToNotUploadedFileList
                                (cursor);

                        if (notUploadedFileList.size() > 0) {
                            for (NotUploadedFile notUploadedFile : notUploadedFileList) {
                                if (needSendNotification(notUploadedFile)) {
                                    NotificationUtils.sendNotUploadedFileNotification(UploadFileService.this,
                                            notUploadedFile);

                                    FilesBL.updateShowNotificationStep(notUploadedFile);
                                }
                            }
                        } else {
                            stopShowNotifiationTimer();
                        }
                    }
                    break;
            }
        }
    }


    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.UPLOAD_TASK_FILE_OPERATION_TAG.equals(operation.getTag())) {
            final NotUploadedFile notUploadedFile = (NotUploadedFile) operation.getEntities().get(0);

            if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
                L.i(TAG, "onNetworkOperation. File uploaded: " + notUploadedFile.getId() + " File name: " +
                        notUploadedFile.getFileName());

                preferencesManager.setUsed3GUploadMonthlySize(preferencesManager.getUsed3GUploadMonthlySize()
                        + (int) (notUploadedFile.getFileSizeB() / 1000));

                FilesBL.deleteNotUploadedFileFromDbById(notUploadedFile.getId()); //Forward to remove the uploaded file

                //TODO Check not uploaded file for this task
                int notUploadedFileCount = FilesBL.getNotUploadedFileCount(notUploadedFile.getTaskId());
                if (notUploadedFileCount == 0) {
                    validateTask(notUploadedFile.getTaskId());
                }

                if (canUploadNextFile(this)) {
                    FilesBL.getFirstNotUploadedFileFromDB(dbHandler, notUploadedFile.get_id(),
                            UIUtils.is3G(UploadFileService.this), COOKIE_UPLOAD_FILE);
                }

            } else {
                L.e(TAG, "onNetworkOperation. File not uploaded: " + notUploadedFile.getId() + " File name: " +
                        notUploadedFile.getFileName());
            }
        } else if (Keys.VALIDATE_TASK_OPERATION_TAG.equals(operation.getTag())) {
            //QuestionsBL.removeQuestionsFromDB(this, task.getSurveyId(), task.getId());
            sendNetworkOperation(apiFacade.getMyTasksOperation());
        }
    }

    private void validateTask(final int taskId) {
        Location location = lm.getLocation();
        if (location != null) {
            sendNetworkOperation(apiFacade.getValidateTaskOperation(taskId, location.getLatitude(),
                    location.getLongitude()));
        } else {
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    sendNetworkOperation(apiFacade.getValidateTaskOperation(taskId, location.getLatitude(),
                            location.getLongitude()));
                }
            });
        }

    }

    private boolean needSendNotification(NotUploadedFile notUploadedFile) {
        boolean result = false;
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

        switch (NotUploadedFile.NotificationStepId.getStep(notUploadedFile.getShowNotificationStepId())) {
            case none:
                result = currentTimeInMillis > notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_15;
                break;
            case min_15:
                result = currentTimeInMillis >= notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_30;
                break;
            case min_30:
                result = currentTimeInMillis >= notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_60;
                break;
            case min_60:
                result = false;
                break;
        }
        return result;
    }

    public static boolean canUploadNextFile(Context context) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        return (preferencesManager.getUsed3GUploadMonthlySize() < preferencesManager.get3GUploadMonthLimit() &&
                UIUtils.is3G(context)) || UIUtils.isWiFi(context);
    }

    public void sendFile(NotUploadedFile notUploadedFile) {
        L.i(TAG, "onQueryComplete. Send file. Uri: " + notUploadedFile.getFileUri());

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UPLOAD_TASK_FILE);
        operation.setTag(Keys.UPLOAD_TASK_FILE_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(notUploadedFile);
        sendNetworkOperation(operation);
    }

    public void sendNetworkOperation(BaseOperation operation) {
        if (operation != null) {
            Intent intent = new Intent(this, UploadFileNetworkService.class);
            intent.putExtra(UploadFileNetworkService.KEY_OPERATION, operation);
            startService(intent);
        }
    }

    class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BaseOperation operation = (BaseOperation) intent.getSerializableExtra(UploadFileNetworkService
                    .KEY_OPERATION);
            if (operation != null) {
                for (NetworkOperationListenerInterface netListener : networkOperationListeners) {
                    if (netListener != null) {
                        netListener.onNetworkOperation(operation);
                    }
                }
            }
        }
    }

    public void addNetworkOperationListener(NetworkOperationListenerInterface listener) {
        networkOperationListeners.add(listener);
    }

    public void removeNetworkOperationListener(NetworkOperationListenerInterface listener) {
        networkOperationListeners.remove(listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        L.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        L.i(TAG, "onDestroy");
        removeNetworkOperationListener(this);
        stopUploadedFilesTimer();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);

        super.onDestroy();
    }
}
