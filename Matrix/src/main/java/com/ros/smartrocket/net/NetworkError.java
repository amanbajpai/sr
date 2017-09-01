package com.ros.smartrocket.net;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.ErrorResponse;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.utils.L;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class NetworkError implements BaseNetworkError {

    private static final String TAG = "NetworkError";
    public static final int NO_INTERNET = -100500;
    public static final int SERVER_INTEERNAL_ERROR = -100600;
    public static final int DEVICE_INTEERNAL_ERROR = -100700;
    public static final int PASSWORD_TOKEN_NOT_VALID_ERROR_CODE = 10002;
    public static final int ACCOUNT_NOT_ACTIVATED_ERROR_CODE = 10020;
    public static final int MAXIMUM_MISSION_ERROR_CODE = 10022;
    public static final int MAXIMUM_CLAIM_PER_MISSION_ERROR_CODE = 10032;
    public static final int HALF_CLAIM_PER_MISSION_CODE = 20000;
    public static final int USER_ALREADY_EXISTS_ERROR_CODE = 10006;
    public static final int TASK_NOT_FOUND_ERROR_CODE = 10014;
    public static final int FILE_ALREADY_UPLOADED_ERROR_CODE = 10053;
    public static final int USER_NOT_FOUND_ERROR_CODE = 10004;
    public static final int USER_ALREADY_EXIST_ERROR_CODE = 10006;
    public static final int YOUR_VERSION_OUTDATED_ERROR_CODE = 10089;
    public static final int FILE_NOT_FOUND = 10091;
    public static final int MAXIMUM_CLAIMS_ERROR_CODE = 10126;
    public static final int SUCCESS = 200;
    public static final int AUTORIZATION_ERROR = 401;
    public static final int LOCAL_UPLOAD_FILE_ERROR = 3701;
    public static final int EXTERNAL_AUTH_NEED_MORE_DATA_ERROR = 10144;
    public static final int EMAIL_SENT_ERROR = 10145;
    public static final int GLOBAL_BLOCK_ERROR = 10146;


    private Integer errorCode;
    private int errorMessageRes;
    private ErrorResponse errorResponse;

    public NetworkError(Throwable t) {
        if (t instanceof HttpException)
            parseResponse(((HttpException) t).response().errorBody());
        else if (t instanceof IOException)
            handleNoInternetError();
        else
            handleUnknownError();
    }

    public NetworkError(ResponseBody errorResponseBody) {
        parseResponse(errorResponseBody);
    }

    private void parseResponse(ResponseBody errorResponseBody) {
        try {
            errorResponse = App.getInstance().getErrorConverter().convert(errorResponseBody);
            handleError();
        } catch (IOException e) {
            L.e(TAG, "ProcessResponse error: " + e.getMessage(), e);
            handleUnknownError();
        }
    }

    private void handleError() {
        if (errorResponse != null) {
            errorCode = errorResponse.getErrorCode();
            switch (errorCode) {
                case PASSWORD_TOKEN_NOT_VALID_ERROR_CODE:
                    errorMessageRes = R.string.password_token_not_valid_error_text;
                    break;
                case USER_NOT_FOUND_ERROR_CODE:
                    errorMessageRes = R.string.user_not_found_error_text;
                    break;
                case USER_ALREADY_EXIST_ERROR_CODE:
                    errorMessageRes = R.string.user_already_exists_error_text;
                    break;
                case YOUR_VERSION_OUTDATED_ERROR_CODE:
                    errorMessageRes = R.string.your_version_outdated;
                    break;
                case MAXIMUM_CLAIMS_ERROR_CODE:
                    errorMessageRes = R.string.error_too_much_claims;
                    break;
                case GLOBAL_BLOCK_ERROR:
                    errorMessageRes = R.string.global_block_error;
                    break;
                case MAXIMUM_CLAIM_PER_MISSION_ERROR_CODE:
                    errorMessageRes = R.string.task_no_longer_available;
                    break;
                case AUTORIZATION_ERROR:
                    errorMessageRes = R.string.error;
                    break;
                case NO_INTERNET:
                    handleNoInternetError();
                    break;
                default:
                    handleUnknownError();
                    break;
            }
        }
    }

    @Override
    public int getErrorMessageRes() {
        return errorMessageRes;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }


    private void handleNoInternetError() {
        errorMessageRes = R.string.no_internet;
        errorCode = NO_INTERNET;
    }

    private void handleUnknownError() {
        errorMessageRes = R.string.error;
        errorCode = SERVER_INTEERNAL_ERROR;
    }
}
