package com.matrix.helpers;

import android.app.Activity;
import android.text.TextUtils;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.db.entity.Login;
import com.matrix.db.entity.Registration;
import com.matrix.db.entity.Subscription;
import com.matrix.net.BaseOperation;
import com.matrix.net.WSUrl;
import com.matrix.utils.UIUtils;

/**
 * Singleton class for work with server API
 */
public class APIFacade {
    private static APIFacade instance = null;


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
        } else {
            UIUtils.showSimpleToast(activity, R.string.credentials_wrong);
        }
    }

    /**
     * @param activity
     * @param email
     * @param fullName
     */
    public void registration(Activity activity, String email, String password, String fullName, String berthDay,
                             Integer countryId, Integer cityId, Boolean agree) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(fullName)) {

            Registration registrationEntity = new Registration();
            registrationEntity.setEmail(email);
            registrationEntity.setPassword(password);
            registrationEntity.setFullName(fullName);
            registrationEntity.setBirthday(berthDay);
            registrationEntity.setCountryId(countryId);
            registrationEntity.setCityId(cityId);

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

    /**
     * @param activity
     */
    public void getSurveys(Activity activity) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.GET_SURVEYS);
        operation.setTag(Keys.GET_SURVEYS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param surveyId
     */
    public void getSurveysTask(Activity activity, Long surveyId) {
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
        operation.setUrl(WSUrl.GET_MY_TASKS);
        operation.setTag(Keys.GET_MY_TASKS_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.GET);
        ((BaseActivity) activity).sendNetworkOperation(operation);
    }

    /**
     * @param activity
     * @param taskId
     */
    public void bookTask(Activity activity, Long taskId) {
        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.BOOK_TASKS, String.valueOf(taskId));
        operation.setTag(Keys.BOOK_TASK_OPERATION_TAG);
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

    public void registerGCMId(String regId) {
        // TODO: Implement registration logic
    }
}
