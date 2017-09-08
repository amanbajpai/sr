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
import com.ros.smartrocket.db.entity.ErrorResponse;
import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.TaskLocation;
import com.ros.smartrocket.db.entity.Token;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.db.entity.WeChatTokenResponse;
import com.ros.smartrocket.db.entity.WeChatUserInfoResponse;
import com.ros.smartrocket.utils.helpers.WriteDataHelper;
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
    private Gson gson;
    private ContentResolver contentResolver;

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
        Gson gson;
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
        contentResolver = getContentResolver();
        gson = new Gson();
        int responseCode = operation.getResponseStatusCode();
        String responseString = operation.getResponseString();
        if (responseCode == BaseNetworkService.SUCCESS && responseString != null) {
            try {
                SparseArray<ContentValues> scheduledTaskContentValuesMap;
                SparseArray<ContentValues> hiddenTaskContentValuesMap;
                SparseArray<ContentValues> validLocationTaskContentValuesMap;
                int url = WSUrl.matchUrl(operation.getUrl());
                switch (url) {
                    case WSUrl.GET_WAVES_ID:
                        Waves waves = gson.fromJson(responseString, Waves.class);

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
                        storeQuestions(operation, url, claimTaskResponse.getQuestions());
                        operation.responseEntities.add(claimTaskResponse);
                        break;
                    case WSUrl.SEND_ANSWERS_ID:
                        break;
                    case WSUrl.VALIDATE_TASK_ID:
                        break;
                    case WSUrl.GET_NEW_TOKEN_ID:
                        Token token = gson.fromJson(responseString, Token.class);
                        getPreferencesManager().setToken(token.getToken());
                        getPreferencesManager().setTokenForUploadFile(token.getToken());
                        getPreferencesManager().setTokenUpdateDate(System.currentTimeMillis());
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
                        Questions questions = gson.fromJson(responseString, Questions.class);
                        storeQuestions(operation, url, questions);
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
            operation.setResponseError(getString(R.string.error));
            operation.setResponseErrorCode(responseCode);

            WriteDataHelper.prepareLogout(this);
            startActivity(IntentUtils.getLoginIntentForLogout(this));
        } else {
            try {
                ErrorResponse error = gson.fromJson(responseString, ErrorResponse.class);
                if (error != null) {
                    operation.setResponseError(error.getErrorMessage());
                    operation.setResponseErrorCode(error.getErrorCode());
                    switch (operation.getResponseErrorCode()) {
                        case PASSWORD_TOKEN_NOT_VALID_ERROR_CODE:
                            operation.setResponseError(getString(R.string.password_token_not_valid_error_text));
                            break;
                        case USER_NOT_FOUND_ERROR_CODE:
                            operation.setResponseError(getString(R.string.user_not_found_error_text));
                            break;
                        case USER_ALREADY_EXIST_ERROR_CODE:
                            operation.setResponseError(getString(R.string.user_already_exists_error_text));
                            break;
                        case YOUR_VERSION_OUTDATED_ERROR_CODE:
                            operation.setResponseError(getString(R.string.your_version_outdated));
                            break;
                        case MAXIMUM_CLAIMS_ERROR_CODE:
                            operation.setResponseError(getString(R.string.error_too_much_claims));
                            break;
                        case GLOBAL_BLOCK_ERROR:
                            operation.setResponseError(getString(R.string.global_block_error));
                            break;
                    }
                }
            } catch (Exception e) {
                L.e(TAG, "ProcessResponse error: " + e.getMessage(), e);
                operation.setResponseError(getString(R.string.error));
                operation.setResponseErrorCode(SERVER_INTEERNAL_ERROR);
            }
        }

    }

    private void storeQuestions(BaseOperation operation, int url, Questions questions) {
        if (questions != null && questions.getQuestions() != null && questions.getQuestions().length > 0) {
            int waveId = operation.getWaveId();
            int taskId = operation.getTaskId();
            int missionId = operation.getMissionId();

            QuestionsBL.removeQuestionsFromDB(this, waveId, taskId, missionId);
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