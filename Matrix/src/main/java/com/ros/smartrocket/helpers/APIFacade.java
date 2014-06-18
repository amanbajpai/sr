package com.ros.smartrocket.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.db.entity.ActivateAccount;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.CheckLocation;
import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.RegisterDevice;
import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.db.entity.SaveReferralCase;
import com.ros.smartrocket.db.entity.SendTaskId;
import com.ros.smartrocket.db.entity.SetPassword;
import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.db.entity.TestPushMessage;
import com.ros.smartrocket.db.entity.UploadPhoto;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkService;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.net.WSUrl;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Singleton class for work with server API
 */
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

    /**
     * @param activity - current activity
     * @param email    - current email
     * @param password - current password
     */
    public void login(Activity activity, String email, String password) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            Login loginEntity = new Login();
            loginEntity.setEmail(email);
            loginEntity.setPassword(password);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.LOGIN);
            operation.setTag(Keys.LOGIN_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(loginEntity);
            ((BaseActivity) activity).sendNetworkOperation(operation);
        }
    }

    /**
     * @param activity - current activity
     * @param email    - current email
     */
    public void forgotPassword(Activity activity, String email) {
        try {
            if (!TextUtils.isEmpty(email)) {
                BaseOperation operation = new BaseOperation();
                operation.setUrl(WSUrl.FORGOT_PASSWORD, URLEncoder.encode(email, "UTF-8"));
                operation.setTag(Keys.FORGOT_PASSWORD_OPERATION_TAG);
                operation.setMethod(BaseOperation.Method.GET);
                ((BaseActivity) activity).sendNetworkOperation(operation);
            } else {
                UIUtils.showSimpleToast(activity, R.string.fill_in_field);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param activity - current activity
     */
    public void getCurrentTermsAndConditionVersion(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_CURRENT_T_AND_C);
        operation.setTag(Keys.GET_CURRENT_T_AND_C_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity - current activity
     * @param email    - current email
     * @param token    - current token
     */
    public void activateAccount(Activity activity, String email, String token) {
        ActivateAccount activateAccountEntity = new ActivateAccount();
        activateAccountEntity.setEmail(email);
        activateAccountEntity.setToken(token);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.ACTIVATE_ACCOUNT);
        operation.setTag(Keys.ACTIVATE_ACCOUNT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(activateAccountEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity - current activity
     * @param email    - current email
     * @param token    - current token
     * @param password - current password
     */
    public void setPassword(Activity activity, String email, String token, String password) {
        SetPassword setPasswordEntity = new SetPassword();
        setPasswordEntity.setEmail(email);
        setPasswordEntity.setPasswordResetToken(token);
        setPasswordEntity.setNewPassword(password);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.SET_PASSWORD);
        operation.setTag(Keys.SET_PASSWORD_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(setPasswordEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity           - current activity
     * @param registrationEntity
     */
    public void registration(Activity activity, Registration registrationEntity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.REGISTRATION);
        operation.setTag(Keys.REGISTRATION_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(registrationEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity    - current activity
     * @param uploadPhoto - photo to upload
     */
    public void uploadPhoto(Activity activity, UploadPhoto uploadPhoto) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UPLOAD_PHOTO);
        operation.setTag(Keys.UPLOAD_PHOTO_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(uploadPhoto);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void checkLocationForRegistration(Activity activity, String country, String city, double latitude,
                                             double longitude) {
        if (!TextUtils.isEmpty(country) && !TextUtils.isEmpty(city)) {
            CheckLocation checkLocationEntity = new CheckLocation();
            checkLocationEntity.setCountry(country);
            checkLocationEntity.setCity(city);
            checkLocationEntity.setLatitude(latitude);
            checkLocationEntity.setLongitude(longitude);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.CHECK_LOCATION);
            operation.setTag(Keys.CHECK_LOCATION_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(checkLocationEntity);
            ((BaseActivity) activity).sendNetworkOperation(operation);
        } else {
            UIUtils.showSimpleToast(activity, R.string.current_location_not_defined);
        }
    }

    /**
     * @param context   - current context
     * @param countryId - current country id from server
     */
    public void getReferralCases(Context context, int countryId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_REFERRAL_CASES, String.valueOf(countryId), preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_REFERRAL_CASES_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) context).sendNetworkOperation(operation);
    }

    /**
     * @param context    - current context
     * @param countryId  - current country id from server
     * @param referralId - current referral id from server
     */
    public void saveReferralCases(Context context, int countryId, int referralId) {
        SaveReferralCase caseEntity = new SaveReferralCase();
        caseEntity.setCountryId(countryId);
        caseEntity.setReferralId(referralId);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.SAVE_REFERRAL_CASE);
        operation.setTag(Keys.SAVE_REFERRAL_CASES_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(caseEntity);
        ((BaseActivity) context).sendNetworkOperation(operation);
    }

    /**
     * @param activity  - current activity
     * @param latitude
     * @param longitude
     */
    public void getWaves(Activity activity, double latitude, double longitude, String countryName, String cityName,
                         int radius) {
        if (activity != null && activity instanceof BaseActivity) {
            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.GET_WAVES, String.valueOf(latitude), String.valueOf(longitude), countryName,
                    cityName, String.valueOf(radius), preferencesManager.getLanguageCode());
            operation.setTag(Keys.GET_WAVES_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.GET);
            ((BaseActivity) activity).sendNetworkOperation(operation);
        } else {
            L.e(TAG, "getWaves with wrong activity");
        }
    }

    /**
     * @param activity - current activity
     * @param waveId   - current waveId
     */
    public void getWaveTasks(Activity activity, Integer waveId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_WAVE_TASKS, String.valueOf(waveId));
        operation.setTag(Keys.GET_WAVE_TASKS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * Get operation for getting my tasks
     */
    public BaseOperation getMyTasksOperation() {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_MY_TASKS, preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_MY_TASKS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        return operation;
    }

    /**
     * @param activity - current activity
     * @param taskId   - tack id to claim
     */
    public void claimTask(Activity activity, Integer taskId, double latitude, double longitude) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(taskId);
        sendTaskId.setLatitude(latitude);
        sendTaskId.setLongitude(longitude);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.CLAIM_TASK);
        operation.setTag(Keys.CLAIM_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param taskId
     */
    public void unclaimTask(Activity activity, Integer taskId) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(taskId);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UNCLAIM_TASK);
        operation.setTag(Keys.UNCLAIM_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * Send this request when all task files were uploaded
     *
     * @param taskId
     * @param latitude
     * @param longitude
     */
    public BaseOperation getValidateTaskOperation(Integer taskId, double latitude, double longitude) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(taskId);
        sendTaskId.setLatitude(latitude);
        sendTaskId.setLongitude(longitude);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.VALIDATE_TASK);
        operation.setTag(Keys.VALIDATE_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        return operation;
    }

    /**
     * Send main file to upload
     *
     * @param context
     * @param notUploadedFile
     */
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


    /**
     * @param activity
     * @param answers
     */
    public void sendAnswers(Activity activity, List<Answer> answers) {
        BaseOperation operation = new BaseOperation();
        operation.setIsArray(true);
        operation.setUrl(WSUrl.SEND_ANSWERS, preferencesManager.getLanguageCode());
        operation.setTag(Keys.SEND_ANSWERS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().addAll(answers);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param taskId
     */
    public void startTask(Activity activity, Integer taskId) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(taskId);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.START_TASK);
        operation.setTag(Keys.START_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param waveId
     */
    public void getQuestions(Activity activity, Integer waveId, Integer taskId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_QUESTIONS, String.valueOf(waveId), preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_QUESTIONS_OPERATION_TAG);
        operation.setWaveId(waveId);
        operation.setTaskId(taskId);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param taskId
     */
    public void getReDoQuestions(Activity activity, Integer waveId, Integer taskId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_REDO_QUESTION, String.valueOf(taskId), preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_REDO_QUESTION_OPERATION_TAG);
        operation.setWaveId(waveId);
        operation.setTaskId(taskId);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param email
     * @param countryName
     * @param cityName
     */
    public void subscribe(Activity activity, String email, String countryName, String cityName) {
        Subscription subscriptionEntity = new Subscription();
        subscriptionEntity.setEmail(email);
        subscriptionEntity.setCountry(countryName);
        subscriptionEntity.setCity(cityName);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.SUBSCRIPTION);
        operation.setTag(Keys.SUBSCRIBE_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(subscriptionEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * We can start registration in GCM not only from Activity
     *
     * @param context
     * @param regId
     */
    public void registerGCMId(Context context, String regId) {
        if (context != null && !TextUtils.isEmpty(regId) && !TextUtils.isEmpty(regId)) {

            RegisterDevice registerDeviceEntity = new RegisterDevice();
            registerDeviceEntity.setDeviceId(PreferencesManager.getInstance().getUUID(context));
            registerDeviceEntity.setRegistrationId(regId);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.GCM_REGISTER_DEVICE);
            operation.setTag(Keys.GCM_REGISTER_DEVICE_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(registerDeviceEntity);

            this.sendRequest(context, operation);
        }
    }

    /**
     * API call for push notification test
     *
     * @param context
     * @param statusType - Notification type
     * @param taskId     - task id
     */
    public void testGCMPushNotification(Context context, Integer statusType, Integer taskId) {
        TestPushMessage pushMessageEntity = new TestPushMessage();
        pushMessageEntity.setStatusType(statusType);
        pushMessageEntity.setTaskId(taskId);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GCM_TEST_PUSH);
        operation.setTag(Keys.GCM_TEST_PUSH_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(pushMessageEntity);

        this.sendRequest(context, operation);
    }

    /**
     * @param activity
     */
    public void getMyAccount(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_MY_ACCOUNT, preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_MY_ACCOUNT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param taskId
     */
    public void rejectTask(Activity activity, Integer taskId) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(taskId);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.REJECT_TASK);
        operation.setTag(Keys.REJECT_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void sendRequest(Context context, BaseOperation operation) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(NetworkService.KEY_OPERATION, operation);
        context.startService(intent);
    }
}
