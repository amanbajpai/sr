package com.ros.smartrocket.net;

import android.app.IntentService;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.*;


public abstract class BaseNetworkService extends IntentService {
    protected static final String TAG = "BaseNetworkService";

    private AndroidHttpClient client;

    private PreferencesManager preferencesManager;

    public static final String KEY_OPERATION = "operation";
    public static final String BROADCAST_ACTION = "operation";

    public static final int NO_INTERNET = -100500;
    public static final int SERVER_INTEERNAL_ERROR = -100600;
    public static final int DEVICE_INTEERNAL_ERROR = -100700;
    public static final int PASSWORD_TOKEN_NOT_VALID_ERROR_CODE = 10002;
    public static final int ACCOUNT_NOT_ACTIVATED_ERROR_CODE = 10020;
    public static final int MAXIMUM_MISSION_ERROR_CODE = 10022;
    public static final int MAXIMUM_CLAIM_PER_MISSION_ERROR_CODE = 10032;
    public static final int USER_ALREADY_EXISTS_ERROR_CODE = 10006;
    public static final int TASK_NOT_FOUND_ERROR_CODE = 10014;
    public static final int FILE_ALREADY_UPLOADED_ERROR_CODE = 10053;
    public static final int USER_NOT_FOUND_ERROR_CODE = 10004;
    public static final int USER_ALREADY_EXIST_ERROR_CODE = 10006;
    public static final int YOUR_VERSION_OUTDATED_ERROR_CODE = 10089;
    public static final int FILE_NOT_FOUND = 10091;
    public static final int QUOTA_IS_EXCEEDE_ERROR = 10066;
    public static final int TASK_ALREADY_SUBMITTED = 10087;
    public static final int SUCCESS = 200;
    public static final int AUTORIZATION_ERROR = 401;
    public static final int LOCAL_UPLOAD_FILE_ERROR = 3701;

    public BaseNetworkService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferencesManager = PreferencesManager.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    protected HttpUriRequest prepareRequest(BaseOperation operation) throws UnsupportedEncodingException {
        HttpUriRequest result;
        String json;
        HttpEntity entity = null;
        BasicHeader contentTypeHeader = null;
        if (!operation.getEntities().isEmpty()) {
            entity = getMultipartEntity(operation);
            if (entity == null) {
                json = getRequestJson(operation);
                if (json != null) {
                    L.i(TAG, "Request body: " + json);
                    entity = new StringEntity(json, "UTF-8");
                    contentTypeHeader = new BasicHeader("Content-type", "application/json");
                }
            } else {
                contentTypeHeader = new BasicHeader("Content-Encoding", "multipart/form-data");
            }
        }
        switch (operation.getMethod()) {
            case GET:
                result = new HttpGet(operation.getRequestUrl());
                break;
            case POST:
                result = new HttpPost(operation.getRequestUrl());
                result.setHeader(contentTypeHeader);
                if (entity != null) {
                    ((HttpPost) result).setEntity(entity);
                }
                break;
            case PUT:
                result = new HttpPut(operation.getRequestUrl());
                json = getRequestJson(operation);
                if (json != null) {
                    result.setHeader(contentTypeHeader);
                    ((HttpPut) result).setEntity(entity);
                }
                break;
            case PATCH:
                result = new HttpPut(operation.getRequestUrl());
                break;
            case DELETE:
                result = new HttpDelete(operation.getRequestUrl());
                break;
            default:
                result = new HttpGet(operation.getRequestUrl());
                break;
        }

        setHeaders(result);

        return result;
    }

    private void setHeaders(HttpUriRequest method) {
        method.addHeader("device-unique", App.getInstance().getDeviceId());
        method.addHeader("device-type", App.getInstance().getDeviceType());
        method.addHeader("device-os-version", App.getInstance().getDeviceApiNumber());
        //method.addHeader("Accept-Encoding", "gzip");

        String token = TextUtils.isEmpty(preferencesManager.getTokenForUploadFile()) ?
                preferencesManager.getToken() :
                preferencesManager.getTokenForUploadFile();
        method.addHeader("Authorization", "Bearer " + token);
        method.addHeader("App-version", BuildConfig.VERSION_NAME);

        try {
            JSONObject settingJsonObject = new JSONObject();
            settingJsonObject.put("CurrentVersion", BuildConfig.VERSION_NAME);
            if (Config.USE_BAIDU) {
                settingJsonObject.put("Region", "AsiaChina");
            } else {
                settingJsonObject.put("Region", "Asia");
            }
            byte[] settingsByteArray = settingJsonObject.toString().getBytes("UTF-8");
            method.addHeader("Settings", Base64.encodeToString(settingsByteArray, Base64.NO_WRAP));
        } catch (Exception e) {
            L.e(TAG, "Add header settings json" + e, e);
        }
    }

    protected abstract String getRequestJson(BaseOperation operation);

    private MultipartEntity getMultipartEntity(BaseOperation operation) {
        MultipartEntity entity = null;
        BaseEntity requestEntity = operation.getEntities().get(0);
        if (requestEntity != null && requestEntity.getClass() == AttachmentRequestEntity.class) {
            entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            String path = ((AttachmentRequestEntity) requestEntity).getAttachmentPath();
            entity.addPart("attachment", new FileBody(new File(path)));
        }
        return entity;
    }

    protected void executeRequest(BaseOperation operation) {
        L.i(TAG, operation.getMethod() + " request to URL: " + operation.getRequestUrl());
        try {
            HttpUriRequest request = prepareRequest(operation);
            if (UIUtils.isOnline(this)) {
                if (request != null) {
                    client = AndroidHttpClient.newInstance("MatrixAndroidApp", this);
                    HttpParams params = client.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 240000);
                    HttpConnectionParams.setSoTimeout(params, 240000);

                    HttpResponse response = client.execute(request);
                    operation = readResponseToOperation(response, operation);
                    client.close();
                }
            } else {
                operation.setResponseStatusCode(NO_INTERNET);
                operation.setResponseError(getString(R.string.no_internet));
            }
        } catch (IOException e) {
            operation.setResponseStatusCode(NO_INTERNET);
            operation.setResponseError(getString(R.string.no_internet));
            L.e(TAG, e.toString(), e);
        }

        processResponse(operation);
    }

    protected BaseOperation readResponseToOperation(HttpResponse response, BaseOperation operation)
            throws IOException {
        String responseString = null;
        if (response != null && response.getStatusLine() != null && response.getEntity() != null) {
            operation.setResponseStatusCode(response.getStatusLine().getStatusCode());
            InputStream ungzippedContent = client.getUngzippedContent(response.getEntity());
            responseString = getStringFromInputStream(ungzippedContent);
        }
        operation.setResponseString(responseString);
        L.i(TAG,
                "Response status code: " + operation.getResponseStatusCode() + "\nResponse: "
                        + operation.getResponseString()
        );
        // logToFile(operation);
        return operation;
    }

    protected void logToFile(BaseOperation operation) {
        File log = new File(Environment.getExternalStorageDirectory() + "/matrix_log.txt");
        try {
            FileWriter fileWriter = new FileWriter(log, true);
            fileWriter.append(operation.toString());
            fileWriter.close();
        } catch (IOException e) {
            L.e(TAG, e.toString(), e);
        }
    }

    /**
     * Called when request finished
     *
     * @param operation - base operation
     * @throws IllegalStateException
     */
    protected abstract void processResponse(BaseOperation operation);

    private String getStringFromInputStream(InputStream is) {

        String result = null;
        if (is != null) {
            StringWriter writer = new StringWriter();
            try {
                IOUtils.copy(is, writer, "utf-8");
                result = writer.toString();
            } catch (IOException e) {
                L.e(TAG, e.toString(), e);
            }
        }
        return result;
    }

    protected void notifyOperationFinished(BaseOperation operation) {
        Intent broadcast = new Intent(BROADCAST_ACTION);
        broadcast.putExtra(KEY_OPERATION, operation);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
    }

    public PreferencesManager getPreferencesManager() {
        return preferencesManager;
    }
}
