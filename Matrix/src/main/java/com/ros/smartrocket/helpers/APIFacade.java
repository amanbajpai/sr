package com.ros.smartrocket.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.*;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkService;
import com.ros.smartrocket.net.WSUrl;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

/**
 * Singleton class for work with server API
 */
public class APIFacade {
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
     * @param activity
     * @param email
     * @param password
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
     * @param activity
     * @param registrationEntity
     */
    public void registration(Activity activity, Registration registrationEntity, boolean agree) {
        if (!TextUtils.isEmpty(registrationEntity.getEmail()) && !TextUtils.isEmpty(registrationEntity.getPassword())
                && agree) {

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.REGISTRATION);
            operation.setTag(Keys.REGISTRETION_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(registrationEntity);
            ((BaseActivity) activity).sendNetworkOperation(operation);
        } else if (!agree) {
            UIUtils.showSimpleToast(activity, R.string.do_you_agree_with_term);
        } else {
            UIUtils.showSimpleToast(activity, R.string.fill_in_field);

        }
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
     * @param activity
     * @param language
     * @param latitude
     * @param longitude
     */
    public void getSurveys(Activity activity, double latitude, double longitude, int radius, String language) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_SURVEYS, String.valueOf(latitude), String.valueOf(longitude),
                String.valueOf(radius), language);
        operation.setTag(Keys.GET_SURVEYS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param surveyId
     */
    public void getSurveysTask(Activity activity, Integer surveyId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_SURVEYS_TASKS, String.valueOf(surveyId));
        operation.setTag(Keys.GET_SURVEYS_TASKS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     */
    public void getMyTasks(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_MY_TASKS, preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_MY_TASKS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param taskId
     */
    public void bookTask(Activity activity, Integer taskId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.BOOK_TASKS, String.valueOf(taskId));
        operation.setTag(Keys.BOOK_TASK_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param surveyId
     */
    public void getQuestions(Activity activity, Integer surveyId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_QUESTIONS, String.valueOf(surveyId), preferencesManager.getLanguageCode());
        operation.setTag(Keys.GET_QUESTIONS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param questions
     */
    public void sendQuestions(Activity activity, ArrayList<Question> questions) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.SEND_QUESTION);
        operation.setTag(Keys.REGISTRETION_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().addAll(questions);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param email
     * @param countryName
     * @param cityName
     */
    public void subscribe(Activity activity, String email, String countryName, String cityName) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(countryName) && !TextUtils.isEmpty(cityName)) {

            Subscription subscriptionEntity = new Subscription();
            subscriptionEntity.setMail(email);
            subscriptionEntity.setCountry(countryName);
            subscriptionEntity.setCountry(cityName);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.SUBSCRIPTION);
            operation.setTag(Keys.SUBSCRIBE_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(subscriptionEntity);
            ((BaseActivity) activity).sendNetworkOperation(operation);
        } else {
            UIUtils.showSimpleToast(activity, R.string.fill_in_field);
        }
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
     * @param regId   - GCM user ID (should be registered in cloud)
     * @param data    - String data
     */
    public void testGCMPushNotification(Context context, String regId, String data) {
        if (!TextUtils.isEmpty(regId) && !TextUtils.isEmpty(data)) {

            PushMessage pushMessageEntity = new PushMessage();
            pushMessageEntity.setMessage(data);
            pushMessageEntity.setTargetDeviceId(regId);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.GCM_TEST_PUSH);
            operation.setTag(Keys.GCM_TEST_PUSH_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(pushMessageEntity);

            this.sendRequest(context, operation);
        } else {
            UIUtils.showSimpleToast(context, R.string.gcm_test_push_noData);
        }
    }

    /**
     * @param activity
     */
    public void getMyAccount(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_MY_ACCOUNT);
        operation.setTag(Keys.GET_MY_ACCOUNT_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }


    private void sendRequest(Context context, BaseOperation operation) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(NetworkService.KEY_OPERATION, operation);
        context.startService(intent);
    }
}
