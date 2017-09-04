package com.ros.smartrocket.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.db.entity.AllowPushNotification;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.RegisterDevice;
import com.ros.smartrocket.db.entity.SendTaskId;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Token;
import com.ros.smartrocket.db.entity.UpdateUser;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkService;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.net.WSUrl;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;

import java.util.List;

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

    public void updateUser(Activity activity, UpdateUser updateUser) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UPDATE_USER);
        operation.setTag(Keys.UPDATE_USER_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(updateUser);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getWaves(Activity activity, double latitude, double longitude, int radius) {
        if (activity != null && activity instanceof BaseActivity) {
            try {
                BaseOperation operation = new BaseOperation();
                operation.setUrl(WSUrl.GET_WAVES, String.valueOf(latitude), String.valueOf(longitude),
                        String.valueOf(radius), preferencesManager.getLanguageCode());
                operation.setTag(Keys.GET_WAVES_OPERATION_TAG);
                operation.setMethod(BaseOperation.Method.GET);
                ((BaseActivity) activity).sendNetworkOperation(operation);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            L.e(TAG, "getWaves with wrong activity");
        }

    }

    public BaseOperation getMyTasksOperation() {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_MY_TASKS, preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_MY_TASKS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        return operation;
    }

    public void claimTask(Activity activity, Task task, double latitude, double longitude) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(task.getId());
        sendTaskId.setLatitude(latitude);
        sendTaskId.setLongitude(longitude);
        BaseOperation operation = new BaseOperation();

        fillTaskData(task, operation);
        operation.setUrl(WSUrl.CLAIM_TASK);
        operation.setTag(Keys.CLAIM_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void unclaimTask(Activity activity, Integer taskId, Integer missionId) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(taskId);
        sendTaskId.setMissionId(missionId);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UNCLAIM_TASK);
        operation.setTag(Keys.UNCLAIM_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        ((BaseActivity) activity).sendNetworkOperation(operation);
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
            ((UploadFileService) context).sendNetworkOperation(operation);
        } else {
            L.e(TAG, "Call sendFile with wrong context");
        }
    }

    public void sendAnswers(Activity activity, List<Answer> answers, Integer missionId) {
        BaseOperation operation = new BaseOperation();
        operation.setIsArray(true);
        operation.setUrl(WSUrl.SEND_ANSWERS, String.valueOf(missionId), preferencesManager.getLanguageCode());
        operation.setTag(Keys.SEND_ANSWERS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().addAll(answers);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void startTask(Activity activity, Task task) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setWaveId(task.getWaveId());
        sendTaskId.setTaskId(task.getId());
        sendTaskId.setMissionId(task.getMissionId());

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.START_TASK);
        operation.setTag(Keys.START_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getQuestions(Activity activity, Task task) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_QUESTIONS, String.valueOf(task.getWaveId()), preferencesManager.getLanguageCode(), String.valueOf(task.getId()));
        operation.setTag(Keys.GET_QUESTIONS_OPERATION_TAG);
        fillTaskData(task, operation);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getReDoQuestions(Activity activity, Task task) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_REDO_QUESTION, String.valueOf(task.getId()), String.valueOf(task.getWaveId()), preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_REDO_QUESTION_OPERATION_TAG);
        fillTaskData(task, operation);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    private void fillTaskData(Task task, BaseOperation operation) {
        operation.setWaveId(task.getWaveId());
        operation.setTaskId(task.getId());
        operation.setMissionId(task.getMissionId());
    }

    public void registerGCMId(Context context, String regId, int providerType) {
        if (context != null && !TextUtils.isEmpty(regId) && !TextUtils.isEmpty(regId)) {

            RegisterDevice registerDeviceEntity = new RegisterDevice();
            registerDeviceEntity.setDeviceId(PreferencesManager.getInstance().getUUID(context));
            registerDeviceEntity.setRegistrationId(regId);
            registerDeviceEntity.setProviderType(providerType);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.GCM_REGISTER_DEVICE);
            operation.setTag(Keys.GCM_REGISTER_DEVICE_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(registerDeviceEntity);

            this.sendRequest(context, operation);
        }
    }

    public void getMyAccount(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_MY_ACCOUNT, preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_MY_ACCOUNT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getSharingData(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_SHARING_DATA, preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_SHARING_DATA_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getNewToken(Activity activity) {
        Token token = new Token();
        token.setToken(preferencesManager.getToken());

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_NEW_TOKEN);
        operation.setTag(Keys.GET_NEW_TOKEN_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(token);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void sendRequest(Context context, BaseOperation operation) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(NetworkService.KEY_OPERATION, operation);
        context.startService(intent);
    }
}
