package com.ros.smartrocket.net;

import android.content.Intent;
import android.net.Uri;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.db.entity.FileToUpload;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.ResponseError;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.SelectImageManager;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * IntentService for API communication
 */
public class UploadFileNetworkService extends BaseNetworkService {
    private static final String TAG = "UploadFileNetworkService";
    public static final String TAG_RECRUITING = "recruiting";
    public static final int MAX_BYTE_SIZE = 1000 * 500;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    public UploadFileNetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BaseOperation operation = (BaseOperation) intent.getSerializableExtra(KEY_OPERATION);
        if (operation != null) {
            if (WSUrl.matchUrl(operation.getUrl()) == WSUrl.UPLOAD_TASK_FILE_ID) {
                NotUploadedFile notUploadedFile = (NotUploadedFile) operation.getEntities().get(0);

                File sourceFile = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
                long mainFileLength = sourceFile.length();

                //Separate main file
                File[] files = separateFile(notUploadedFile);
                if (files != null) {
                    try {
                        for (int i = 0; i < files.length; i++) {

                            BaseOperation tempOperation = getSendTempFileOperation(files[i], notUploadedFile,
                                    mainFileLength);
                            executeRequest(tempOperation);

                            int responseCode = tempOperation.getResponseStatusCode();
                            String responseString = tempOperation.getResponseString();
                            if (responseCode == BaseNetworkService.SUCCESS && responseString != null) {
                                L.i(TAG, "Upload temp file success: " + files[i].getName());

                                notUploadedFile.setPortion(notUploadedFile.getPortion() + 1);
                                notUploadedFile.setFileCode(new JSONObject(responseString).getString("FileCode"));

                                FilesBL.updatePortionAndFileCode(notUploadedFile.getId(), notUploadedFile.getPortion(),
                                        notUploadedFile.getFileCode());

                                files[i].delete();
                                operation.setResponseStatusCode(responseCode);
                            } else {
                                operation.setResponseStatusCode(NO_INTERNET);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                executeRequest(operation);
            }
            notifyOperationFinished(operation);
        }
    }

    public BaseOperation getSendTempFileOperation(File file, NotUploadedFile notUploadedFile, long mainFileLength) {
        FileToUpload uploadFileEntity = new FileToUpload();
        uploadFileEntity.setTaskId(notUploadedFile.getTaskId());
        uploadFileEntity.setQuestionId(notUploadedFile.getQuestionId());
        uploadFileEntity.setFileOffset((long) MAX_BYTE_SIZE * notUploadedFile.getPortion());
        uploadFileEntity.setFileCode(notUploadedFile.getFileCode());
        uploadFileEntity.setFilename(notUploadedFile.getFileName());
        uploadFileEntity.setFileLength(mainFileLength);
        uploadFileEntity.setFileBase64String(SelectImageManager.getFileAsString(file));
        uploadFileEntity.setLanguageCode(preferencesManager.getLanguageCode());

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UPLOAD_TASK_FILE);
        operation.setTag(Keys.UPLOAD_TASK_TEMP_FILE_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(uploadFileEntity);
        return operation;
    }

    public File[] separateFile(NotUploadedFile notUploadedFile) {
        try {
            File sourceFile = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
            byte[] soureByteArray = FileUtils.readFileToByteArray(sourceFile);

            int portainCount = (int) Math.ceil((double) soureByteArray.length / MAX_BYTE_SIZE);

            if (notUploadedFile.getPortion() == portainCount) {
                notUploadedFile.setPortion(0);
            }

            File[] files = new File[portainCount - notUploadedFile.getPortion()];

            for (int i = notUploadedFile.getPortion(); i < portainCount; i++) {

                int startPosition = MAX_BYTE_SIZE * i;
                int length = MAX_BYTE_SIZE;

                if (startPosition + length > soureByteArray.length) {
                    length = soureByteArray.length - startPosition;
                }

                byte[] tempByteArray = new byte[length];

                System.arraycopy(soureByteArray, startPosition, tempByteArray, 0, length);

                File tempFile = SelectImageManager.getTempFile(this);

                try {
                    FileOutputStream fos = new FileOutputStream(tempFile, false);
                    fos.write(tempByteArray);
                    fos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                files[i - notUploadedFile.getPortion()] = tempFile;
            }

            return files;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
        if (responseCode == BaseNetworkService.SUCCESS && responseString != null) {
            try {
                /*ContentResolver contentResolver = getContentResolver();
                HashMap<Integer, ContentValues> scheduledTaskContentValuesMap;*/
                switch (WSUrl.matchUrl(operation.getUrl())) {
                    case WSUrl.VALIDATE_TASK_ID:
                        //SendTaskId sendedTaskId = (SendTaskId) operation.getEntities().get(0);

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
                    operation.setResponseErrorCode(error.getErrorCode());
                }
            } catch (JsonSyntaxException e) {
                operation.setResponseError(getString(R.string.error));
            }
        }
    }
}
