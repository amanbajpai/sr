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
import com.ros.smartrocket.db.store.QuestionStore;
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
                int url = WSUrl.matchUrl(operation.getUrl());
                switch (url) {
                    case WSUrl.GCM_REGISTER_DEVICE_ID:
                        getPreferencesManager().setBoolean(Keys.GCM_IS_GCMID_REGISTERED, true);
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


}