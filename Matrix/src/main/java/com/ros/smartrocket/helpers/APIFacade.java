package com.ros.smartrocket.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.db.entity.ActivateAccount;
import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.db.entity.AllowPushNotification;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.CheckLocation;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.RegisterDevice;
import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.db.entity.SaveReferralCase;
import com.ros.smartrocket.db.entity.SendTaskId;
import com.ros.smartrocket.db.entity.SetPassword;
import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.db.entity.Token;
import com.ros.smartrocket.db.entity.UpdateUser;
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

    public void login(Activity activity, Login loginEntity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.LOGIN);
        operation.setTag(Keys.LOGIN_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(loginEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void sendTandC(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.POST_TERMS_AND_CONDITIONS);
        operation.setTag(Keys.POST_T_AND_C_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getAppVersion(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_APP_VERSION);
        operation.setTag(Keys.GET_VERSION_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void forgotPassword(Activity activity, String email) {
        try {
            if (!TextUtils.isEmpty(email)) {
                BaseOperation operation = new BaseOperation();
                operation.setUrl(WSUrl.FORGOT_PASSWORD, URLEncoder.encode(email, "UTF-8"), preferencesManager.getLanguageCode());
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

    public void registration(Activity activity, Registration registrationEntity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.REGISTRATION, preferencesManager.getLanguageCode());
        operation.setTag(Keys.REGISTRATION_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(registrationEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void updateUser(Activity activity, UpdateUser updateUser) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UPDATE_USER);
        operation.setTag(Keys.UPDATE_USER_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(updateUser);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void checkLocationForRegistration(Activity activity, String country, String city, String district,
                                             double latitude, double longitude) {
        CheckLocation checkLocationEntity = new CheckLocation();
        checkLocationEntity.setCountry(country);
        checkLocationEntity.setCity(city);
        checkLocationEntity.setDistrict(district);
        checkLocationEntity.setLatitude(latitude);
        checkLocationEntity.setLongitude(longitude);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.CHECK_LOCATION);
        operation.setTag(Keys.CHECK_LOCATION_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(checkLocationEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getReferralCases(Context context, int countryId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_REFERRAL_CASES, String.valueOf(countryId), preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_REFERRAL_CASES_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) context).sendNetworkOperation(operation);
    }

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

    public void startTask(Activity activity, Integer waveId, Integer taskId, Integer missionId) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setWaveId(waveId);
        sendTaskId.setTaskId(taskId);
        sendTaskId.setMissionId(missionId);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.START_TASK);
        operation.setTag(Keys.START_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(sendTaskId);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getQuestions(Activity activity, Integer waveId, Integer taskId, Integer missionId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_QUESTIONS, String.valueOf(waveId), preferencesManager.getLanguageCode(), String.valueOf(taskId));
        operation.setTag(Keys.GET_QUESTIONS_OPERATION_TAG);
        operation.setWaveId(waveId);
        operation.setTaskId(taskId);
        operation.setMissionId(missionId);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getReDoQuestions(Activity activity, Integer waveId, Integer taskId, Integer missionId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_REDO_QUESTION, String.valueOf(taskId), String.valueOf(missionId), preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_REDO_QUESTION_OPERATION_TAG);
        operation.setWaveId(waveId);
        operation.setTaskId(taskId);
        operation.setMissionId(missionId);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void subscribe(Activity activity, String email, String countryName, String cityName,
                          Double latitude, Double longitude, Integer districtId, Integer countryId,
                          Integer cityId) {
        Subscription subscriptionEntity = new Subscription();
        subscriptionEntity.setEmail(email);
        subscriptionEntity.setCountry(countryName);
        subscriptionEntity.setCity(cityName);
        subscriptionEntity.setLatitude(latitude);
        subscriptionEntity.setLongitude(longitude);
        subscriptionEntity.setDistrictId(districtId);
        subscriptionEntity.setCountryId(countryId);
        subscriptionEntity.setCityId(cityId);

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.SUBSCRIPTION);
        operation.setTag(Keys.SUBSCRIBE_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(subscriptionEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
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

    public void sendActivity(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.SEND_ACTIVITY);
        operation.setTag(Keys.SEND_ACTIVITY_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void cashingOut(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.CASHING_OUT);
        operation.setTag(Keys.CASHING_OUT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
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

    public void getAliPayAccount(Activity activity) {
        Token token = new Token();
        token.setToken(preferencesManager.getToken());

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_ALIPAY_ACCOUNT);
        operation.setTag(Keys.GET_ALIPAY_ACCOUNT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void integrateAliPayAccount(Activity activity, AliPayAccount aliPayAccount) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_ALIPAY_ACCOUNT);
        operation.setTag(Keys.INTEGRATE_ALIPAY_ACCOUNT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(aliPayAccount);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getNationalIdAccount(Activity activity) {
        Token token = new Token();
        token.setToken(preferencesManager.getToken());
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_NATIONAL_ID_ACCOUNT);
        operation.setTag(Keys.GET_NATIONAL_ID_ACCOUNT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void integrateNationalIdAccount(Activity activity, NationalIdAccount nationalIdAccount) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_NATIONAL_ID_ACCOUNT);
        operation.setTag(Keys.INTEGRATE_NATIONAL_ID_ACCOUNT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(nationalIdAccount);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void allowPushNotification(Activity activity, boolean allow) {
        AllowPushNotification allowPushNotification = new AllowPushNotification();
        if (allow) {
            allowPushNotification.allow();
        } else {
            allowPushNotification.disallow();
        }

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.ALLOW_PUSH_NOTIFICATION);
        operation.setTag(Keys.ALLOW_PUSH_NOTIFICATION_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(allowPushNotification);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void checkEmail(Activity activity, String email) {
        BaseOperation operation = new BaseOperation();
        String userEmail = "";
        try {
            userEmail = URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        operation.setUrl(WSUrl.GET_CHECK_EMAIL, userEmail);
        operation.setTag(Keys.GET_CHECK_EMAIL_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void setPromoCode(Activity activity, String srCode) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.POST_PROMO_CODE, srCode);
        operation.setTag(Keys.POST_PROMO_CODE_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void externalAuth(Activity activity, ExternalAuthorize externalAuthorizeEntity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.POST_EXTERNAL_AUTHORIZE, preferencesManager.getLanguageCode());
        operation.setTag(Keys.POST_EXTERNAL_AUTH_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(externalAuthorizeEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getWeChatToken(Activity activity, String code) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_WECHAT_TOKEN, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET, code);
        operation.setTag(Keys.GET_WECHAT_TOKEN_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void getWeChatInfo(Activity activity, String token, String openId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_WECHAT_USER_INFO, token, openId);
        operation.setTag(Keys.GET_WECHAT_INFO_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void externalRegistration(Activity activity, ExternalAuthorize externalAuthorizeEntity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.POST_EXTERNAL_REGISTER, preferencesManager.getLanguageCode());
        operation.setTag(Keys.POST_EXTERNAL_REG_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(externalAuthorizeEntity);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    public void closeAccount(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.CLOSE_ACCOUNT);
        operation.setTag(Keys.CLOSE_ACCOUNT_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }
}
