package com.matrix.net;

import android.content.ContentResolver;
import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.matrix.R;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.*;
import com.matrix.utils.L;

import java.util.ArrayList;


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
                    case WSUrl.GET_ALL_TASKS_ID:
                        Task[] tasks = gson.fromJson(responseString, Task[].class);
                        for (Task task : tasks) {
                            contentResolver.insert(TaskDbSchema.CONTENT_URI, task.toContentValues());
                        }

                        //operation.responseEntities.addAll(new ArrayList<Task>(Arrays.asList(tasks)));
                        break;

                    case WSUrl.LOGIN_ID:
                        LoginResponse loginResponse = gson.fromJson(responseString, LoginResponse.class);
                        operation.responseEntities.add(loginResponse);
                        preferencesManager.setString(TOKEN, loginResponse.getToken());
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
                if (error != null && error.getError() != null) {
                    operation.setResponseError(error.getError());
                }
            } catch (JsonSyntaxException e) {
                operation.setResponseError(getString(R.string.error));
            }
        }
    }
}
