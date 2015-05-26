package com.ros.smartrocket.net;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.util.SparseArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.*;
import com.ros.smartrocket.helpers.WriteDataHelper;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;

import java.util.ArrayList;

/**
 * IntentService for API communication
 */
public class NetworkService extends BaseNetworkService {
    private static final String TAG = "NetworkService";
    public static final String TAG_RECRUITING = "recruiting";

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            BaseOperation operation = (BaseOperation) intent.getSerializableExtra(KEY_OPERATION);
            if (operation != null) {
                executeRequest(operation);

                notifyOperationFinished(operation);
            }
        }
    }

    protected String getRequestJson(BaseOperation operation) {
        Gson gson = null;
        if (TAG_RECRUITING.equals(operation.getTag())) {
            gson = new GsonBuilder().disableHtmlEscaping().create();
        } else {
            gson = new Gson();
        }
        String json = null;
        ArrayList<BaseEntity> entityList = operation.getEntities();
        if (!entityList.isEmpty()) {
            json = gson.toJson(entityList.size() > 1 || operation.getIsArray() ? entityList : entityList.get(0));
        }
        L.i(TAG, "json: " + json);
        return json;
    }

    protected void processResponse(BaseOperation operation) {
        Gson gson = new Gson();
        int responseCode = operation.getResponseStatusCode();
        String responseString = operation.getResponseString();
        if (responseCode == BaseNetworkService.SUCCESS && responseString != null) {
            try {
                ContentResolver contentResolver = getContentResolver();
                SparseArray<ContentValues> scheduledTaskContentValuesMap;
                SparseArray<ContentValues> hiddenTaskContentValuesMap;
                SparseArray<ContentValues> validLocationTaskContentValuesMap;
                int url = WSUrl.matchUrl(operation.getUrl());
                switch (url) {
                    case WSUrl.GET_WAVES_ID:
                        Waves waves = gson.fromJson(responseString, Waves.class);

                        //Get tasks with 'scheduled' status id
                        scheduledTaskContentValuesMap = TasksBL.getScheduledTaskHashMap(contentResolver);
                        hiddenTaskContentValuesMap = TasksBL.getHiddenTaskHashMap(contentResolver);

                        TasksBL.removeNotMyTask(contentResolver);
                        WavesBL.saveWaveAndTaskFromServer(contentResolver, waves, false);

                        //Update task status id
                        TasksBL.updateTasksByContentValues(contentResolver, scheduledTaskContentValuesMap);
                        TasksBL.updateTasksByContentValues(contentResolver, hiddenTaskContentValuesMap);

                        break;
                    case WSUrl.GET_MY_TASKS_ID:
                        Waves myTasksWaves = gson.fromJson(responseString, Waves.class);

                        //Get tasks with 'scheduled' status id
                        scheduledTaskContentValuesMap = TasksBL.getScheduledTaskHashMap(contentResolver);
                        hiddenTaskContentValuesMap = TasksBL.getHiddenTaskHashMap(contentResolver);
                        validLocationTaskContentValuesMap = TasksBL.getValidLocationTaskHashMap(contentResolver);

                        TasksBL.removeAllMyTask(contentResolver);
                        WavesBL.saveWaveAndTaskFromServer(contentResolver, myTasksWaves, true);

                        //Update task status id
                        TasksBL.updateTasksByContentValues(contentResolver, scheduledTaskContentValuesMap);
                        TasksBL.updateTasksByContentValues(contentResolver, hiddenTaskContentValuesMap);
                        TasksBL.updateTasksByContentValues(contentResolver, validLocationTaskContentValuesMap);
                        break;

                    case WSUrl.CLAIM_TASKS_ID:
                        break;
                    case WSUrl.SEND_ANSWERS_ID:
                        break;
                    case WSUrl.VALIDATE_TASK_ID:
                        break;
                    case WSUrl.LOGIN_ID:
                        LoginResponse loginResponse = gson.fromJson(responseString, LoginResponse.class);
                        operation.responseEntities.add(loginResponse);
                        getPreferencesManager().setToken(loginResponse.getToken());
                        break;
                    case WSUrl.GET_CURRENT_T_AND_C_ID:
                        TermsAndConditionVersion currentVersion = gson.fromJson(responseString,
                                TermsAndConditionVersion.class);
                        operation.responseEntities.add(currentVersion);
                        break;
                    case WSUrl.GET_REFERRAL_CASES_ID:
                        ReferralCases referralCases = gson.fromJson(responseString, ReferralCases.class);
                        operation.responseEntities.add(referralCases);
                        break;
                    case WSUrl.CHECK_LOCATION_ID:
                        CheckLocationResponse checkLocationResponse = gson.fromJson(responseString,
                                CheckLocationResponse.class);
                        operation.responseEntities.add(checkLocationResponse);
                        break;
                    case WSUrl.REGISTRATION_ID:
                        RegistrationResponse registrationResponse = gson.fromJson(responseString,
                                RegistrationResponse.class);
                        operation.responseEntities.add(registrationResponse);
                        break;
                    case WSUrl.GCM_REGISTER_DEVICE_ID:
                        getPreferencesManager().setBoolean(Keys.GCM_IS_GCMID_REGISTERED, true);
                        break;
                    case WSUrl.GCM_TEST_PUSH_ID:
                        L.i(TAG, "GCM [test push send]");
//                        SubscriptionResponse subscriptionResponse = gson.fromJson(responseString,
//                                SubscriptionResponse.class);
//                        operation.responseEntities.add(subscriptionResponse);
                        break;
                    case WSUrl.GET_MY_ACCOUNT_ID:
                        MyAccount myAccountResponse = gson.fromJson(responseString, MyAccount.class);
                        operation.responseEntities.add(myAccountResponse);
                        App.getInstance().setMyAccount(myAccountResponse);
                        break;
                    case WSUrl.GET_QUESTIONS_ID:
                    case WSUrl.GET_REDO_QUESTION_ID:
                        int waveId = operation.getWaveId();
                        int taskId = operation.getTaskId();

                        QuestionsBL.removeQuestionsFromDB(this, waveId, taskId);

                        Questions questions = gson.fromJson(responseString, Questions.class);

                        int i = 1;
                        for (Question question : questions.getQuestions()) {
                            question.setTaskId(taskId);
                            AskIf[] askIfArray = question.getAskIfArray();
                            if (askIfArray != null) {
                                question.setAskIf(gson.toJson(askIfArray));
                            }

                            TaskLocation taskLocation = question.getTaskLocationObject();
                            if (taskLocation != null) {
                                taskLocation.setCustomFields(gson.toJson(taskLocation.getCustomFieldsMap()));
                                question.setTaskLocation(gson.toJson(taskLocation));
                            }
                            if (WSUrl.GET_REDO_QUESTION_ID == url) {
                                question.setOrderId(i);
                            }
                            contentResolver.insert(QuestionDbSchema.CONTENT_URI, question.toContentValues());

                            contentResolver.delete(AnswerDbSchema.CONTENT_URI,
                                    AnswerDbSchema.Columns.QUESTION_ID + "=? and " + AnswerDbSchema.Columns.TASK_ID
                                            + "=?",
                                    new String[]{String.valueOf(question.getId()), String.valueOf(taskId)}
                            );

                            if (question.getAnswers() != null) {
                                for (Answer answer : question.getAnswers()) {
                                    answer.setRandomId();
                                    answer.setQuestionId(question.getId());
                                    answer.setTaskId(taskId);
                                    contentResolver.insert(AnswerDbSchema.CONTENT_URI, answer.toContentValues());
                                }
                            } else {
                                Answer answer = new Answer();
                                answer.setRandomId();
                                answer.setQuestionId(question.getId());
                                answer.setTaskId(taskId);
                                contentResolver.insert(AnswerDbSchema.CONTENT_URI, answer.toContentValues());
                            }
                            i++;
                        }

                        break;
                    case WSUrl.GET_SHARING_DATA_ID:
                        Sharing sharing = gson.fromJson(responseString, Sharing.class);
                        operation.responseEntities.add(sharing);
                        break;
                    default:
                        break;
                }
            } catch (JsonSyntaxException e) {
                L.e(TAG, e.toString(), e);
            }
        } else if (responseCode == NO_INTERNET) {
            operation.setResponseError(getString(R.string.no_internet));
            operation.setResponseErrorCode(responseCode);
        } else if (responseCode == AUTORIZATION_ERROR) {
            operation.setResponseError(getString(R.string.no_internet));
            operation.setResponseErrorCode(responseCode);

            WriteDataHelper.prepareLogout(this);
            startActivity(IntentUtils.getLoginIntentForLogout(this));
        } else {
            try {
                ResponseError error = gson.fromJson(responseString, ResponseError.class);
                if (error != null) {
                    operation.setResponseError(error.getErrorMessage());
                    operation.setResponseErrorCode(error.getErrorCode());

                    if (operation.getResponseErrorCode() == PASSWORD_TOKEN_NOT_VALID_ERROR_CODE) {
                        operation.setResponseError(getString(R.string.password_token_not_valid_error_text));

                    } else if (operation.getResponseErrorCode() == USER_NOT_FOUND_ERROR_CODE) {
                        operation.setResponseError(getString(R.string.user_not_found_error_text));

                    } else if (operation.getResponseErrorCode() == USER_ALREADY_EXIST_ERROR_CODE) {
                        operation.setResponseError(getString(R.string.user_already_exists_error_text));

                    } else if (operation.getResponseErrorCode() == YOUR_VERSION_OUTDATED_ERROR_CODE) {
                        operation.setResponseError(getString(R.string.your_version_outdated));

                    }
                }
            } catch (Exception e) {
                L.e(TAG, "ProcessResponse error: " + e.getMessage(), e);
                operation.setResponseError(getString(R.string.error));
                operation.setResponseErrorCode(SERVER_INTEERNAL_ERROR);
            }
        }

    }
}
