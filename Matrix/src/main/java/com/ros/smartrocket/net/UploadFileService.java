package com.ros.smartrocket.net;

import android.app.Service;
import android.content.*;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.SendTaskId;
import com.ros.smartrocket.db.entity.ServerLog;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.io.File;
import java.util.*;

/**
 * For upload file:
 * 1. Save file uri to NotUploadedFile table
 * 2. Start service with tag ACTION_CHECK_NOT_UPLOADED_FILES
 * startService(new Intent(this, UploadFileService.class).setAction(Keys.ACTION_CHECK_NOT_UPLOADED_FILES));
 */

public class UploadFileService extends Service implements NetworkOperationListenerInterface {
    private static final String TAG = UploadFileService.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private List<NetworkOperationListenerInterface>
            networkOperationListeners = new ArrayList<>();
    private AsyncQueryHandler dbHandler;
    private BroadcastReceiver receiver;

    private Timer uploadFilesTimer;
    private Timer showNotificationTimer;
    private boolean uploadingFiles = false;

    private static final String COOKIE_UPLOAD_FILE = "upload_file";
    private static final String COOKIE_SHOW_NOTIFICATION = "show_notification";
    private static final String COOKIE_CHECK_NOT_UPLOAD_FILE_COUNT = "not_upload_file_count";

    private static final int WAIT_START_TIMER_IN_MILLISECONDS = 5000;
    private static final int MINUTE_IN_MILLISECONDS_15 = 1000 * 60 * 15;
    private static final int MINUTE_IN_MILLISECONDS_30 = 1000 * 60 * 30;
    private static final int MINUTE_IN_MILLISECONDS_60 = 1000 * 60 * 60;

    public UploadFileService() {
    }

    @Override
    public void onCreate() {
        L.i(TAG, "onCreate");

        dbHandler = new DbHandler(getContentResolver());
        receiver = new NetworkBroadcastReceiver();
        IntentFilter filter = new IntentFilter(NetworkService.BROADCAST_ACTION);

        addNetworkOperationListener(this);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
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

                            L.i(TAG, "In timer. Start UploadFilesTimer");
                            if (!uploadingFiles && canUploadNextFile(UploadFileService.this)) {
                                L.i(TAG, "Can upload file");
                                FilesBL.getFirstNotUploadedFileFromDB(dbHandler, 0,
                                        UIUtils.is3G(UploadFileService.this), COOKIE_UPLOAD_FILE);

                            }

                        }
                    }, WAIT_START_TIMER_IN_MILLISECONDS, Config.CHECK_NOT_UPLOADED_FILE_MILLISECONDS);

                    showNotificationTimer = new Timer();
                    showNotificationTimer.schedule(new TimerTask() {
                        public void run() {

                            L.i(TAG, "In timer. Start showNotificationTimer");
                            FilesBL.getNotUploadedFilesFromDB(dbHandler, COOKIE_SHOW_NOTIFICATION);

                        }
                    }, WAIT_START_TIMER_IN_MILLISECONDS, Config.SHOW_NOTIFICATION_FOR_NOT_UPLOADED_FILE_MILLISECONDS);
                } catch (Exception e) {
                    L.e(TAG, "StartCheckNotUploadedFilesTimer error: " + e.getMessage(), e);
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
                            L.i(TAG, "Send file to upload Date: " + UIUtils.longToString(System.currentTimeMillis(), 2));
                            uploadingFiles = true;
                            apiFacade.sendFile(UploadFileService.this, notUploadedFile);
                        } else {
                            uploadingFiles = false;
                            FilesBL.getNotUploadedFilesCountFromDB(dbHandler, COOKIE_CHECK_NOT_UPLOAD_FILE_COUNT);

                        }
                    } else if (cookie.equals(COOKIE_SHOW_NOTIFICATION)) {
                        List<NotUploadedFile> notUploadedFileList = FilesBL.convertCursorToNotUploadedFileList
                                (cursor);

                        if (!notUploadedFileList.isEmpty()) {
                            for (NotUploadedFile notUploadedFile : notUploadedFileList) {
                                if (needSendNotification(notUploadedFile)) {
                                    NotificationUtils.startFileNotUploadedNotificationActivity(UploadFileService.this,
                                            notUploadedFile.getTaskName());

                                    FilesBL.updateShowNotificationStep(notUploadedFile);
                                }
                            }
                        } else {
                            stopShowNotifiationTimer();
                        }
                    } else if (cookie.equals(COOKIE_CHECK_NOT_UPLOAD_FILE_COUNT)) {
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            if (cursor.getInt(0) == 0) {
                                stopUploadedFilesTimer();
                            }
                        } else {
                            stopUploadedFilesTimer();
                        }
                    }
                    break;
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    final Task task = TasksBL.convertCursorToTask(cursor);

                    Location location = new Location(LocationManager.NETWORK_PROVIDER);
                    location.setLatitude(task.getLatitudeToValidation());
                    location.setLongitude(task.getLongitudeToValidation());

                    MatrixLocationManager.getAddressByLocation(location, new MatrixLocationManager.GetAddressListener() {
                        @Override
                        public void onGetAddressSuccess(Location location, String countryName, String cityName, String districtName) {
                            sendValidateLog("Send task to validation. ", task.getId(),
                                    task.getMissionId(), task.getLatitudeToValidation(),
                                    task.getLongitudeToValidation(), cityName);

                            sendNetworkOperation(apiFacade.getValidateTaskOperation(task.getId(), task.getMissionId(),
                                    task.getLatitudeToValidation(), task.getLongitudeToValidation(), cityName));
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.UPLOAD_TASK_FILE_OPERATION_TAG.equals(operation.getTag())) {
            final NotUploadedFile notUploadedFile = (NotUploadedFile) operation.getEntities().get(0);

            int responseCode = operation.getResponseStatusCode();

            if (responseCode == BaseNetworkService.SUCCESS) {
                L.i(TAG, "onNetworkOperation. File uploaded: " + notUploadedFile.getId()
                        + " File name: " + notUploadedFile.getFileName()
                        + " Date: " + UIUtils.longToString(System.currentTimeMillis(), 2));

                preferencesManager.setUsed3GUploadMonthlySize(preferencesManager.getUsed3GUploadMonthlySize()
                        + (int) (notUploadedFile.getFileSizeB() / 1024));

                FilesBL.deleteNotUploadedFileFromDbById(notUploadedFile.getId()); //Forward to remove the uploaded file

                int notUploadedFileCount = FilesBL.getNotUploadedFileCount(notUploadedFile.getTaskId());
                if (notUploadedFileCount == 0) {
                    validateTask(notUploadedFile);
                }

            } else if (responseCode == BaseNetworkService.FILE_ALREADY_UPLOADED_ERROR_CODE) {
                //Forward to remove the uploaded file
                sendFileLog("Error. File not uploaded. ErrorCode = " + responseCode +
                        " ErrorText = " + operation.getResponseError(), notUploadedFile);

                FilesBL.deleteNotUploadedFileFromDbById(notUploadedFile.getId());

            } else {
                L.e(TAG, "onNetworkOperation. File not uploaded: " + notUploadedFile.getId() + " File name: "
                        + notUploadedFile.getFileName() + " Response Error" + operation.getResponseError() + " Date: " +
                        UIUtils.longToString(System.currentTimeMillis(), 2));
                sendFileLog("Error. File not uploaded. ErrorCode = " + responseCode +
                                " ErrorText = " + operation.getResponseError(),
                        notUploadedFile);

                UIUtils.showSimpleToast(this, operation.getResponseError());
            }

            if (responseCode != BaseNetworkService.NO_INTERNET && canUploadNextFile(this)) {
                FilesBL.getFirstNotUploadedFileFromDB(dbHandler, notUploadedFile.get_id(),
                        UIUtils.is3G(UploadFileService.this), COOKIE_UPLOAD_FILE);
            } else {
                uploadingFiles = false;
            }
        } else if (Keys.VALIDATE_TASK_OPERATION_TAG.equals(operation.getTag())) {
            int responseCode = operation.getResponseStatusCode();

            if (responseCode == BaseNetworkService.SUCCESS || responseCode == BaseNetworkService.TASK_NOT_FOUND_ERROR_CODE) {
                SendTaskId sendTask = (SendTaskId) operation.getEntities().get(0);

                sendValidateLog("Success Validate task. ", sendTask.getTaskId(),
                        sendTask.getMissionId(), sendTask.getLatitude(),
                        sendTask.getLongitude(), sendTask.getCityName());

                sendNetworkOperation(apiFacade.getMyTasksOperation());
            } else {
                SendTaskId sendTask = (SendTaskId) operation.getEntities().get(0);

                sendValidateLog("Error. Can not Validate task. ErrorCode = " + responseCode +
                                " ErrorText = " + operation.getResponseError(), sendTask.getTaskId(),
                        sendTask.getMissionId(), sendTask.getLatitude(),
                        sendTask.getLongitude(), sendTask.getCityName());
            }
        }
    }

    private void validateTask(final NotUploadedFile notUploadedFile) {
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(notUploadedFile.getLatitudeToValidation());
        location.setLongitude(notUploadedFile.getLongitudeToValidation());

        MatrixLocationManager.getAddressByLocation(location, new MatrixLocationManager.GetAddressListener() {
            @Override
            public void onGetAddressSuccess(Location location, String countryName, String cityName, String districtName) {
                sendValidateLog("Send task to validation. ", notUploadedFile.getTaskId(),
                        notUploadedFile.getMissionId(), notUploadedFile.getLatitudeToValidation(),
                        notUploadedFile.getLongitudeToValidation(), cityName);

                sendNetworkOperation(apiFacade.getValidateTaskOperation(notUploadedFile.getTaskId(),
                        notUploadedFile.getMissionId(), location.getLatitude(),
                        location.getLongitude(), cityName));
            }
        });
    }

    public void sendValidateLog(String command, Integer taskId, Integer missionId, double latitude, double longitude,
                                String cityName) {
        String userName = preferencesManager.getLastEmail();

        String message = command +
                " taskId = " + taskId +
                " missionId = " + missionId +
                " latitude = " + latitude +
                " longitude = " + longitude +
                " cityName = " + cityName + " \n\n " +
                " networkType = " + UIUtils.getConnectedNetwork(this) + " \n\n " +
                " useWiFiOnly = " + preferencesManager.getUseOnlyWiFiConnaction() +
                " 3GUploadMonthLimit = " + preferencesManager.get3GUploadMonthLimit() +
                " 3GUploadTaskLimit = " + preferencesManager.get3GUploadTaskLimit() +
                " used3GUploadMonthlySize = " + preferencesManager.getUsed3GUploadMonthlySize() +
                " useLocationServices = " + preferencesManager.getUseLocationServices() +
                " useSaveImageToCameraRoll = " + preferencesManager.getUseSaveImageToCameraRoll();
        String type = ServerLog.LogType.VALIDATE_TASK.getType();
        sendNetworkOperation(apiFacade.getSendLogOperation(UploadFileService.this, userName, message, type));
    }

    public void sendFileLog(String command, NotUploadedFile notUploadedFile) {
        String userName = preferencesManager.getLastEmail();

        String message = command +
                " taskId = " + notUploadedFile.getTaskId() +
                " missionId = " + notUploadedFile.getMissionId() +
                " latitude = " + notUploadedFile.getLatitudeToValidation() +
                " longitude = " + notUploadedFile.getLongitudeToValidation() + " \n\n " +
                " fileExist = " + new File(Uri.parse(notUploadedFile.getFileUri()).getPath()).exists() +
                " fileName = " + notUploadedFile.getFileName() +
                " filePath = " + notUploadedFile.getFileUri() +
                " fileCode = " + notUploadedFile.getFileCode() +
                " addedToUploadDateTime = " + notUploadedFile.getAddedToUploadDateTime() +
                " fileSize (byte) = " + notUploadedFile.getFileSizeB() +
                " portion = " + notUploadedFile.getPortion() +
                " use3G = " + notUploadedFile.getUse3G() + "\n\n" +
                " networkType = " + UIUtils.getConnectedNetwork(this) + " \n\n " +
                " useWiFiOnly = " + preferencesManager.getUseOnlyWiFiConnaction() +
                " 3GUploadMonthLimit = " + preferencesManager.get3GUploadMonthLimit() +
                " 3GUploadTaskLimit = " + preferencesManager.get3GUploadTaskLimit() +
                " used3GUploadMonthlySize = " + preferencesManager.getUsed3GUploadMonthlySize() +
                " useLocationServices = " + preferencesManager.getUseLocationServices() +
                " useSaveImageToCameraRoll = " + preferencesManager.getUseSaveImageToCameraRoll();
        String type = ServerLog.LogType.FILE_UPLOAD.getType();
        sendNetworkOperation(apiFacade.getSendLogOperation(UploadFileService.this, userName, message, type));
    }

    private boolean needSendNotification(NotUploadedFile notUploadedFile) {
        boolean result = false;
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

        switch (NotUploadedFile.NotificationStepId.getStep(notUploadedFile.getShowNotificationStepId())) {
            case NONE:
                result = currentTimeInMillis > notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_15;
                break;
            case MIN_15:
                result = currentTimeInMillis >= notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_30;
                break;
            case MIN_30:
                result = currentTimeInMillis >= notUploadedFile.getAddedToUploadDateTime() + MINUTE_IN_MILLISECONDS_60;
                break;
            case MIN_60:
                result = false;
                break;
            default:
                break;
        }
        return result;
    }

    public static boolean canUploadNextFile(Context context) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        return (UIUtils.is3G(context) && !preferencesManager.getUseOnlyWiFiConnaction()) || UIUtils.isWiFi(context);
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
