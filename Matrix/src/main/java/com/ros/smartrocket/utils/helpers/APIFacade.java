package com.ros.smartrocket.utils.helpers;

import android.content.Context;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.SendTaskId;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.net.WSUrl;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;

public class APIFacade {
    private static final String TAG = "APIFacade";
    private static APIFacade instance = null;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();


    public static APIFacade getInstance() {
        if (instance == null) {
            instance = new APIFacade();
        }
        return instance;
    }

    private APIFacade() {
    }

    public BaseOperation getValidateTaskOperation(Integer waveId, Integer taskId, Integer missionId, double latitude,
                                                  double longitude, String cityName) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(taskId);
        sendTaskId.setWaveId(waveId);
        sendTaskId.setMissionId(missionId);
        sendTaskId.setLatitude(latitude);
        sendTaskId.setLongitude(longitude);
        sendTaskId.setCityName(cityName);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.VALIDATE_TASK);
        operation.setTag(Keys.VALIDATE_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        return operation;
    }

    public void sendFile(Context context, NotUploadedFile notUploadedFile) {
        if (context instanceof UploadFileService) {
            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.UPLOAD_TASK_FILE);
            operation.setTag(Keys.UPLOAD_TASK_FILE_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(notUploadedFile);
            //((UploadFileService) context).sendNetworkOperation(operation);
        } else {
            L.e(TAG, "Call sendFile with wrong context");
        }
    }
}
