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
import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.db.entity.AllowPushNotification;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.AppVersion;
import com.ros.smartrocket.db.entity.AskIf;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.db.entity.Category;
import com.ros.smartrocket.db.entity.CheckEmail;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.db.entity.ClaimTaskResponse;
import com.ros.smartrocket.db.entity.ExternalAuthResponse;
import com.ros.smartrocket.db.entity.LoginResponse;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Questions;
import com.ros.smartrocket.db.entity.ReferralCases;
import com.ros.smartrocket.db.entity.RegistrationResponse;
import com.ros.smartrocket.db.entity.ResponseError;
import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.TaskLocation;
import com.ros.smartrocket.db.entity.TermsAndConditionVersion;
import com.ros.smartrocket.db.entity.Token;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.db.entity.WeChatTokenResponse;
import com.ros.smartrocket.db.entity.WeChatUserInfoResponse;
import com.ros.smartrocket.helpers.WriteDataHelper;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.MyLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Override
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
                        L.i("Network Service", "WAVES FROM SERVER " + waves.getWaves().length);

                        try {
                            //Get tasks with 'scheduled' status id
                            scheduledTaskContentValuesMap = TasksBL.getScheduledTaskHashMap(contentResolver);
                            hiddenTaskContentValuesMap = TasksBL.getHiddenTaskHashMap(contentResolver);

                            Wave[] tempWaves = waves.getWaves();
                            for (Wave tempWave : tempWaves) {
                                Task[] tempTasks = tempWave.getTasks();
                                for (int j = 1; j < tempTasks.length; j++) {
                                    if (tempTasks[j].getPrice() != tempTasks[j - 1].getPrice()) {
                                        tempWave.setContainsDifferentRate(true);
                                        break;
                                    }
                                }
                            }

                            for (Wave tempWave : tempWaves) {
                                Task[] tempTasks = tempWave.getTasks();
                                double min = tempTasks[0].getPrice();
                                for (Task tempTask : tempTasks) {
                                    if (tempTask.getPrice() < min) {
                                        min = tempTask.getPrice();
                                    }
                                }
                                tempWave.setRate(min);
                            }

                            TasksBL.removeNotMyTask(contentResolver);
                            WavesBL.saveWaveAndTaskFromServer(contentResolver, waves, false);

                            //Update task status id
                            TasksBL.updateTasksByContentValues(contentResolver, scheduledTaskContentValuesMap);
                            TasksBL.updateTasksByContentValues(contentResolver, hiddenTaskContentValuesMap);

                        } catch (Exception e) {
                            MyLog.logStackTrace(e);
                            L.e(TAG, "Error updating data TASK and WAVE DB");
                            operation.setResponseErrorCode(DEVICE_INTEERNAL_ERROR);
                        }

                        break;
                    case WSUrl.GET_MY_TASKS_ID:
                        Waves myTasksWaves = gson.fromJson(responseString, Waves.class);

                        try {
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

                        } catch (Exception e) {
                            MyLog.logStackTrace(e);
                            L.e(TAG, "Error updating data TASK and WAVE DB");
                            operation.setResponseErrorCode(DEVICE_INTEERNAL_ERROR);
                        }
                        break;

                    case WSUrl.CLAIM_TASKS_ID:
                        ClaimTaskResponse claimTaskResponse = gson.fromJson(responseString, ClaimTaskResponse.class);
                        operation.responseEntities.add(claimTaskResponse);
                        break;
                    case WSUrl.SEND_ANSWERS_ID:
                        break;
                    case WSUrl.VALIDATE_TASK_ID:
                        break;
                    case WSUrl.LOGIN_ID:
                        LoginResponse loginResponse = gson.fromJson(responseString, LoginResponse.class);
                        operation.responseEntities.add(loginResponse);
                        getPreferencesManager().setToken(loginResponse.getToken());
                        getPreferencesManager().setTokenForUploadFile(loginResponse.getToken());
                        getPreferencesManager().setTokenUpdateDate(System.currentTimeMillis());
                        break;
                    case WSUrl.GET_NEW_TOKEN_ID:
                        Token token = gson.fromJson(responseString, Token.class);
                        getPreferencesManager().setToken(token.getToken());
                        getPreferencesManager().setTokenForUploadFile(token.getToken());
                        getPreferencesManager().setTokenUpdateDate(System.currentTimeMillis());
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
                        int missionId = operation.getMissionId();

                        QuestionsBL.removeQuestionsFromDB(this, waveId, taskId, missionId);
                        Questions questions = gson.fromJson(responseString, Questions.class);
                        questions.setQuestions(QuestionsBL.sortQuestionsByOrderId(questions.getQuestions()));

                        int i = 1;
                        for (Question question : questions.getQuestions()) {
                            if (i != 1 && question.getShowBackButton()) {
                                question.setPreviousQuestionOrderId(i - 1);
                            }
                            i = insertQuestion(gson, contentResolver, url, taskId, missionId, i, question);
                        }

                        if (questions.getMissionSize() != null) {
                            WavesBL.updateWave(waveId, questions.getMissionSize());
                        }
                        break;
                    case WSUrl.GET_SHARING_DATA_ID:
                        Sharing sharing = gson.fromJson(responseString, Sharing.class);
                        operation.responseEntities.add(sharing);
                        break;
                    case WSUrl.GET_ALIPAY_ACCOUNT_ID:
                        AliPayAccount aliPayAccount = gson.fromJson(responseString, AliPayAccount.class);
                        operation.responseEntities.add(aliPayAccount);
                        break;
                    case WSUrl.GET_NATIONAL_ID_ACCOUNT_ID:
                        NationalIdAccount nationalIdAccount = gson.fromJson(responseString, NationalIdAccount.class);
                        operation.responseEntities.add(nationalIdAccount);
                        break;
                    case WSUrl.ALLOW_PUSH_NOTIFICATION_ID:
                        AllowPushNotification allowPushNotification = gson.fromJson(responseString,
                                AllowPushNotification.class);
                        operation.responseEntities.add(allowPushNotification);
                        break;
                    case WSUrl.APP_VERSION_ID:
                        AppVersion appVersion = gson.fromJson(responseString, AppVersion.class);
                        operation.responseEntities.add(appVersion);
                        break;
                    case WSUrl.CHECK_EMAIL_ID:
                        CheckEmail checkEmail = gson.fromJson(responseString, CheckEmail.class);
                        operation.responseEntities.add(checkEmail);
                        break;
                    case WSUrl.EXTERNAL_AUTH_ID:
                        ExternalAuthResponse authResponse = gson.fromJson(responseString, ExternalAuthResponse.class);
                        operation.responseEntities.add(authResponse);
                        getPreferencesManager().setToken(authResponse.getToken());
                        getPreferencesManager().setTokenForUploadFile(authResponse.getToken());
                        getPreferencesManager().setTokenUpdateDate(System.currentTimeMillis());
                        break;
                    case WSUrl.WECHAT_TOKEN_ID:
                        WeChatTokenResponse tokenResponse = gson.fromJson(responseString, WeChatTokenResponse.class);
                        operation.responseEntities.add(tokenResponse);
                        break;
                    case WSUrl.WECHAT_USER_INFO_ID:
                        WeChatUserInfoResponse weChatUserInfoResponse = gson.fromJson(responseString, WeChatUserInfoResponse.class);
                        operation.responseEntities.add(weChatUserInfoResponse);
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

                    } else if (operation.getResponseErrorCode() == MAXIMUM_CLAIMS_ERROR_CODE) {
                        operation.setResponseError(getString(R.string.error_too_much_claims));

                    }
                }
            } catch (Exception e) {
                L.e(TAG, "ProcessResponse error: " + e.getMessage(), e);
                operation.setResponseError(getString(R.string.error));
                operation.setResponseErrorCode(SERVER_INTEERNAL_ERROR);
            }
        }

    }

    private List<Product> makeProductList(Question question) {
        List<Product> productList = new ArrayList<>();

        Category[] categoriesArray = question.getCategoriesArray();
        if (categoriesArray != null) {
            for (Category category : categoriesArray) {
                if (category.getProducts() != null) {
                    Collections.addAll(productList, category.getProducts());
                }
            }
        }

        return productList;
    }

    private int insertQuestion(Gson gson, ContentResolver contentResolver, int url, int taskId, int missionId, int i,
                               Question question) {
        List<ContentValues> answersValues = new ArrayList<>();
        List<ContentValues> questionValues = new ArrayList<>();

        question = prepareQuestion(gson, url, taskId, missionId, i, question);

        questionValues.add(question.toContentValues());

        contentResolver.delete(AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Columns.QUESTION_ID + "=? and " + AnswerDbSchema.Columns.TASK_ID + "=?",
                new String[]{String.valueOf(question.getId()), String.valueOf(taskId)});
        if (question.getChildQuestions() != null && question.getChildQuestions().length > 0) {
            List<Product> productList = makeProductList(question);
            int j = 1;
            for (Question childQuestion : question.getChildQuestions()) {
                contentResolver.delete(AnswerDbSchema.CONTENT_URI,
                        AnswerDbSchema.Columns.QUESTION_ID + "=? and " + AnswerDbSchema.Columns.TASK_ID + "=?",
                        new String[]{String.valueOf(childQuestion.getId()), String.valueOf(taskId)});
                childQuestion = prepareQuestion(gson, url, taskId, missionId, j, childQuestion);
                questionValues.add(childQuestion.toContentValues());
                answersValues.addAll(getAnswerValues(taskId, missionId, childQuestion, productList));
                j++;
            }
        }
        answersValues.addAll(getAnswerValues(taskId, missionId, question, null));
        if (!answersValues.isEmpty()) {
            ContentValues[] valuesArray = new ContentValues[answersValues.size()];
            valuesArray = answersValues.toArray(valuesArray);
            contentResolver.bulkInsert(AnswerDbSchema.CONTENT_URI, valuesArray);
        }
        if (!questionValues.isEmpty()) {
            ContentValues[] valuesArray = new ContentValues[questionValues.size()];
            valuesArray = questionValues.toArray(valuesArray);
            contentResolver.bulkInsert(QuestionDbSchema.CONTENT_URI, valuesArray);
        }
        i++;
        return i;
    }


    private List<ContentValues> getAnswerValues(int taskId, int missionId, Question question,
                                                List<Product> productList) {
        List<ContentValues> answersValues = new ArrayList<>();
        if (productList != null) {
            // Insert answers for MassAudit subquestions
            for (Product product : productList) {
                if (question.getProductId() == null || product.getId().equals(question.getProductId())) {
                    if (question.getAnswers() != null && question.getAnswers().length > 0) {
                        for (Answer answer : question.getAnswers()) {
                            answersValues.add(prepareAnswer(taskId, missionId, question, answer, product.getId()).toContentValues());
                        }
                    } else {
                        Answer answer = new Answer();
                        answersValues.add(prepareAnswer(taskId, missionId, question, answer, product.getId()).toContentValues());
                    }
                }
            }
        } else {
            if (question.getAnswers() != null && question.getAnswers().length > 0) {
                for (Answer answer : question.getAnswers()) {
                    answersValues.add(prepareAnswer(taskId, missionId, question, answer, null).toContentValues());
                }
            } else {
                Answer answer = new Answer();
                answersValues.add(prepareAnswer(taskId, missionId, question, answer, null).toContentValues());
            }
        }
        return answersValues;
    }

    private Answer prepareAnswer(int taskId, int missionId, Question question, Answer answer,
                                 Integer productId) {
        if (question.getType() == Question.QuestionType.MAIN_SUB_QUESTION.getTypeId()
                && question.isRedo()) {
            answer.setChecked(false);
        }
        answer.setRandomId();
        answer.setProductId(productId);
        answer.setQuestionId(question.getId());
        answer.setTaskId(taskId);
        answer.setMissionId(missionId);
        return answer;
    }


    private Question prepareQuestion(Gson gson, int url, int taskId, int missionId, int i,
                                     Question question) {
        question.setTaskId(taskId);
        question.setMissionId(missionId);

        AskIf[] askIfArray = question.getAskIfArray();
        if (askIfArray != null) {
            question.setAskIf(gson.toJson(askIfArray));
        }

        Category[] categoriesArray = question.getCategoriesArray();
        if (categoriesArray != null) {
            question.setCategories(gson.toJson(categoriesArray));
        }

        TaskLocation taskLocation = question.getTaskLocationObject();
        if (taskLocation != null) {
            taskLocation.setCustomFields(gson.toJson(taskLocation.getCustomFieldsMap()));
            question.setTaskLocation(gson.toJson(taskLocation));
        }
        if (WSUrl.GET_REDO_QUESTION_ID == url) {
            question.setOrderId(i);
        }
        return question;
    }
}