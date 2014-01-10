package com.ros.smartrocket.net;

import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.db.entity.FileToUpload;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
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
    //private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private ArrayList<NetworkOperationListenerInterface> networkOperationListeners = new ArrayList<NetworkOperationListenerInterface>();
    private AsyncQueryHandler dbHandler;
    private BroadcastReceiver receiver;
    private IntentFilter filter;

    private Timer refreshMessageTimer;

    @Override
    public void onCreate() {
        L.i(TAG, "onCreate");

        dbHandler = new DbHandler(getContentResolver());
        receiver = new NetworkBroadcastReceiver();
        filter = new IntentFilter(NetworkService.BROADCAST_ACTION);

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
        if (refreshMessageTimer != null) {
            refreshMessageTimer.cancel();
        }

        new Thread() {
            public void run() {
                try {
                    refreshMessageTimer = new Timer();
                    refreshMessageTimer.schedule(new TimerTask() {
                        public void run() {
                            if (canUploadNextFile(UploadFileService.this)) {
                                FilesBL.getLastNotUploadedFileFromDB(dbHandler);
                            }
                        }
                    }, 5000, Config.CHECK_NOT_UPLOADED_FILE_MILLISECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void stopCheckNotUploadedFilesTimer() {
        if (refreshMessageTimer != null) {
            refreshMessageTimer.cancel();
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

                    NotUploadedFile notUploadedFile = FilesBL.convertCursorToNotUploadedFile(cursor);

                    if (notUploadedFile != null) {
                        FileToUpload fileToUpload = new FileToUpload();
                        fileToUpload.setSurveyId(notUploadedFile.getSurveyId());
                        fileToUpload.setTaskId(notUploadedFile.getTaskId());
                        fileToUpload.setQuestionId(notUploadedFile.getQuestionId());
                        fileToUpload.setFileBase64(getFileAsString(Uri.parse(notUploadedFile.getFileUri())));

                        sendFile(fileToUpload);

                        L.i(TAG, "onQueryComplete. Send file. Uri: " + notUploadedFile.getFileUri());
                    } else {
                        //Stop upload file timer
                        stopCheckNotUploadedFilesTimer();
                    }
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.UPLOAD_QUESTION_FILE_OPERATION_TAG.equals(operation.getTag())) {
                L.i(TAG, "onNetworkOperation. File uploaded");

                FileToUpload fileToUpload = (FileToUpload) operation.getEntities().get(0);
                FilesBL.deleteNotUploadedFileFromDbById(fileToUpload.get_id()); //Forward to remove the uploaded file
                if (canUploadNextFile(this)) {
                    FilesBL.getLastNotUploadedFileFromDB(dbHandler);
                }
            }
        } else {
            L.e(TAG, "onNetworkOperation. File not uploaded");
        }
    }

    public String getFileAsString(Uri uri) {
        String resultString = "";
        File file = new File(uri.getPath());
        try {
            byte[] fileAsBytesArray = FileUtils.readFileToByteArray(file);
            resultString = Base64.encodeToString(fileAsBytesArray, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    public static boolean canUploadNextFile(Context context) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        return preferencesManager.getUsed3GUploadSize() < preferencesManager.get3GUploadPackageLimit() || UIUtils
                .isWiFi(context);
    }

    public void sendFile(FileToUpload fileToUpload) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UPLOAD_QUESTION_FILE);
        operation.setTag(Keys.UPLOAD_QUESTION_FILE_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(fileToUpload);
        sendNetworkOperation(operation);
    }

    public void sendNetworkOperation(BaseOperation operation) {
        if (operation != null) {
            Intent intent = new Intent(this, NetworkService.class);
            intent.putExtra(NetworkService.KEY_OPERATION, operation);
            startService(intent);
        }
    }

    class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BaseOperation operation = (BaseOperation) intent.getSerializableExtra(NetworkService.KEY_OPERATION);
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
        stopCheckNotUploadedFilesTimer();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);

        super.onDestroy();
    }
}
