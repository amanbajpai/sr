package com.ros.smartrocket.net;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.SurveyDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.*;
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
        BaseOperation operation = (BaseOperation) intent.getSerializableExtra(KEY_OPERATION);
        if (operation != null) {
            executeRequest(operation);

            notifyOperationFinished(operation);
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
        if (entityList.size() > 0) {
            json = gson.toJson(entityList.size() > 1 ? entityList : entityList.get(0));
        }
        L.i(TAG, "json: " + json);
        return json;
    }

    protected void processResponse(BaseOperation operation) {
        Gson gson = new Gson();

        int responseCode = operation.getResponseStatusCode();
        String responseString = operation.getResponseString();
        if (responseCode == 200 && responseString != null) {
            try {
                ContentResolver contentResolver = getContentResolver();
                switch (WSUrl.matchUrl(operation.getUrl())) {
                    case WSUrl.GET_SURVEYS_ID:
                        Surveys surveys = gson.fromJson(responseString, Surveys.class);

                        for (Survey survey : surveys.getSurveys()) {
                            contentResolver.insert(SurveyDbSchema.CONTENT_URI, survey.toContentValues());

                            ArrayList<ContentValues> vals = new ArrayList<ContentValues>();
                            for (Task task : survey.getTasks()) {
                                vals.add(task.toContentValues());
                            }
                            ContentValues[] bulk = new ContentValues[vals.size()];
                            contentResolver.bulkInsert(TaskDbSchema.CONTENT_URI, vals.toArray(bulk));
                        }

                        break;
                    case WSUrl.GET_MY_TASKS_ID:
                        Location currentLocation = App.getInstance().getLocationManager().getLocation();
                        Surveys myTasksSurveys = gson.fromJson(responseString, Surveys.class);

                        for (Survey survey : myTasksSurveys.getSurveys()) {
                            contentResolver.insert(SurveyDbSchema.CONTENT_URI, survey.toContentValues());

                            ArrayList<ContentValues> vals = new ArrayList<ContentValues>();
                            for (Task task : survey.getTasks()) {
                                Location temp = new Location(LocationManager.NETWORK_PROVIDER);
                                temp.setLatitude(task.getLatitude());
                                temp.setLongitude(task.getLongitude());
                                task.setIsMy(true);

                                if (currentLocation != null) {
                                    task.setDistance(currentLocation.distanceTo(temp));
                                }
                                vals.add(task.toContentValues());
                            }
                            ContentValues[] bulk = new ContentValues[vals.size()];
                            contentResolver.bulkInsert(TaskDbSchema.CONTENT_URI, vals.toArray(bulk));
                        }
                        break;

                    case WSUrl.BOOK_TASKS_ID:
                        BookTaskResponse bookTaskResponse = gson.fromJson(responseString, BookTaskResponse.class);
                        operation.responseEntities.add(bookTaskResponse);
                        break;
                    case WSUrl.LOGIN_ID:
                        LoginResponse loginResponse = gson.fromJson(responseString, LoginResponse.class);
                        operation.responseEntities.add(loginResponse);
                        preferencesManager.setToken(loginResponse.getToken());
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
                    case WSUrl.SUBSCRIPTION_ID:
                        SubscriptionResponse subscriptionResponse = gson.fromJson(responseString,
                                SubscriptionResponse.class);
                        operation.responseEntities.add(subscriptionResponse);
                        break;
                    case WSUrl.GCM_REGISTER_DEVICE_ID:
                        preferencesManager.setBoolean(Keys.GCM_IS_GCMID_REGISTERED, true);
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
                        int taskId = operation.getTaskId();

                        Questions questions = gson.fromJson(responseString, Questions.class);

                        for (Question question : questions.getQuestions()) {
                            contentResolver.insert(QuestionDbSchema.CONTENT_URI, question.toContentValues());

                            contentResolver.delete(AnswerDbSchema.CONTENT_URI,
                                    AnswerDbSchema.Columns.QUESTION_ID + "=?", new String[]{String.valueOf(question
                                    .getId())});

                            if (question.getAnswers() != null) {
                                ArrayList<ContentValues> answersCvList = new ArrayList<ContentValues>();
                                for (Answer answer : question.getAnswers()) {
                                    answer.setQuestionId(question.getId());
                                    answer.setTaskId(taskId);
                                    answersCvList.add(answer.toContentValues());
                                }

                                ContentValues[] answersCvArray = new ContentValues[answersCvList.size()];
                                contentResolver.bulkInsert(AnswerDbSchema.CONTENT_URI, answersCvList.toArray(answersCvArray));
                            } else {
                                Answer answer = new Answer();
                                answer.setQuestionId(question.getId());
                                answer.setTaskId(taskId);
                                contentResolver.insert(AnswerDbSchema.CONTENT_URI, answer.toContentValues());
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (JsonSyntaxException e) {
                L.e(TAG, e.toString());
            }
        } else if (responseCode == NO_INTERNET) {
            operation.setResponseError(getString(R.string.no_internet));
        } else {
            try {
                ResponseError error = gson.fromJson(responseString, ResponseError.class);
                if (error != null && error.getErrorMessage() != null) {
                    operation.setResponseError(error.getErrorMessage());
                }
            } catch (JsonSyntaxException e) {
                operation.setResponseError(getString(R.string.error));
            }
        }
    }
}
