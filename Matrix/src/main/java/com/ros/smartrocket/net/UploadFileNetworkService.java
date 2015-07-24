package com.ros.smartrocket.net;

import android.content.Intent;
import android.net.Uri;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.db.entity.*;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.SelectImageManager;
import com.ros.smartrocket.utils.UIUtils;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * IntentService for API communication
 */
public class UploadFileNetworkService extends BaseNetworkService {
    private static final String TAG = "UploadFileNetworkService";
    public static final String TAG_RECRUITING = "recruiting";
    public static int MAX_BYTE_SIZE = 1000 * 16;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();

    public UploadFileNetworkService() {
        super("NetworkService");

        if (!BuildConfig.USE_BAIDU) {
            MAX_BYTE_SIZE = 32 * 1000;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            BaseOperation operation = (BaseOperation) intent.getSerializableExtra(KEY_OPERATION);
            if (operation != null) {
                if (WSUrl.matchUrl(operation.getUrl()) == WSUrl.UPLOAD_TASK_FILE_ID) {
                    NotUploadedFile notUploadedFile = (NotUploadedFile) operation.getEntities().get(0);

                    File[] files = null;

                    try {
                        File sourceFile = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
                        if (!sourceFile.exists()) {
                            InputStream inputStream = getAssets().open("images/no_image5.jpg");
                            FileUtils.copyInputStreamToFile(inputStream, sourceFile);
                        }

                        long mainFileLength = sourceFile.length();

                        sendLog("Start send Main file", notUploadedFile, sourceFile, ServerLog.LogType.FILE_UPLOAD);

                        //Separate main file
                        files = separateFile(notUploadedFile);
                        for (int i = 0; i < files.length; i++) {

                            L.i(TAG, "Upload file part " + i + ": " + files[i].getName() + " Date: " +
                                    UIUtils.longToString(System.currentTimeMillis(), 2));

                            BaseOperation tempOperation = getSendTempFileOperation(files[i], notUploadedFile,
                                    mainFileLength);
                            executeRequest(tempOperation);

                            int responseCode = tempOperation.getResponseStatusCode();
                            String responseString = tempOperation.getResponseString();
                            Integer responseErrorCode = tempOperation.getResponseErrorCode();
                            if (responseCode == BaseNetworkService.SUCCESS && responseString != null) {
                                L.i(TAG, "Upload file part " + i + " SUCCESS: " + files[i].getName() + " Date: " +
                                        UIUtils.longToString(System.currentTimeMillis(), 2)
                                        + " FileCode: " + new JSONObject(responseString).getString("FileCode"));

                                notUploadedFile.setPortion(notUploadedFile.getPortion() + 1);
                                notUploadedFile.setFileCode(new JSONObject(responseString).getString("FileCode"));

                                FilesBL.updatePortionAndFileCode(notUploadedFile.getId(),
                                        notUploadedFile.getPortion(), notUploadedFile.getFileCode());

                                files[i].delete();
                                operation.setResponseStatusCode(responseCode);

                            } else if (responseErrorCode != null && (responseErrorCode == BaseNetworkService
                                    .TASK_NOT_FOUND_ERROR_CODE ||
                                    responseErrorCode == BaseNetworkService.FILE_ALREADY_UPLOADED_ERROR_CODE ||
                                    responseErrorCode == BaseNetworkService.FILE_NOT_FOUND)) {

                                sendLog("Error send package file. ErrorCode = " + responseErrorCode +
                                        " ErrorText = " + responseString, notUploadedFile, files[i], ServerLog
                                        .LogType.PACKAGE_UPLOAD);
                                files[i].delete();
                                operation.setResponseStatusCode(responseCode);
                                operation.setResponseErrorCode(responseErrorCode);
                                break;

                            } else {
                                cleanFiles(files);
                                sendLog("Error send package file. ErrorCode = " + responseErrorCode +
                                        " ErrorText = " + responseString, notUploadedFile, files[i], ServerLog
                                        .LogType.PACKAGE_UPLOAD);
                                operation.setResponseStatusCode(responseCode);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        L.e(TAG, "Upload file error" + e.getMessage() + " Date: " +
                                UIUtils.longToString(System.currentTimeMillis(), 2), e);
                        cleanFiles(files);
                        sendLog("Error send package file. Exception = " + e.getMessage(), notUploadedFile, null,
                                ServerLog.LogType.PACKAGE_UPLOAD);

                        operation.setResponseErrorCode(BaseNetworkService.LOCAL_UPLOAD_FILE_ERROR);
                    }
                } else {
                    executeRequest(operation);
                }
                notifyOperationFinished(operation);
            }
        }
    }

    private void cleanFiles(File[] files) {
        if (files != null) {
            for (File file : files) {
                FileUtils.deleteQuietly(file);
            }
        }
    }

    public BaseOperation getSendTempFileOperation(File file, NotUploadedFile notUploadedFile, long mainFileLength) {
        FileToUpload uploadFileEntity = new FileToUpload();
        uploadFileEntity.setTaskId(notUploadedFile.getTaskId());
        uploadFileEntity.setMissionId(notUploadedFile.getMissionId());
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

    public void sendLog(String command, NotUploadedFile notUploadedFile, File file, ServerLog.LogType logType) {
        String userName = preferencesManager.getLastEmail();

        String message = command +
                " taskId = " + notUploadedFile.getTaskId() +
                " missionId = " + notUploadedFile.getMissionId() +
                " latitude = " + notUploadedFile.getLatitudeToValidation() +
                " longitude = " + notUploadedFile.getLongitudeToValidation() + " \n\n " +
                " fileCode = " + notUploadedFile.getFileCode() +
                " addedToUploadDateTime (MainFile) = " + notUploadedFile.getAddedToUploadDateTime() +
                " portion = " + notUploadedFile.getPortion() + " \n\n ";

        if (file != null) {
            message +=
                    " fileExist = " + file.exists() +
                            " fileName = " + file.getName() +
                            " filePath = " + file.getAbsolutePath() +
                            " fileSize (byte) = " + file.length();
        }

        message +=
                " networkType = " + UIUtils.getConnectedNetwork(this) + " \n\n " +
                        " useWiFiOnly = " + preferencesManager.getUseOnlyWiFiConnaction() +
                        " 3GUploadMonthLimit = " + preferencesManager.get3GUploadMonthLimit() +
                        " 3GUploadTaskLimit = " + preferencesManager.get3GUploadTaskLimit() +
                        " used3GUploadMonthlySize = " + preferencesManager.getUsed3GUploadMonthlySize() +
                        " useLocationServices = " + preferencesManager.getUseLocationServices() +
                        " useSaveImageToCameraRoll = " + preferencesManager.getUseSaveImageToCameraRoll();

        executeRequest(apiFacade.getSendLogOperation(UploadFileNetworkService.this, userName, message, logType
                .getType()));
    }

    private File[] separateFile(NotUploadedFile notUploadedFile) throws IOException {
        System.gc();
        File sourceFile = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
        byte[] soureByteArray = FileUtils.readFileToByteArray(sourceFile);

        int portainCount = (int) Math.ceil((double) soureByteArray.length / MAX_BYTE_SIZE);

        if (notUploadedFile.getPortion() == portainCount) {
            notUploadedFile.setPortion(0);
            notUploadedFile.setFileCode(null);
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

            FileOutputStream fos = new FileOutputStream(tempFile, false);
            fos.write(tempByteArray);
            fos.close();

            files[i - notUploadedFile.getPortion()] = tempFile;
        }

        return files;
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
        return json;
    }

    protected void processResponse(BaseOperation operation) {
        Gson gson = new Gson();

        int responseCode = operation.getResponseStatusCode();
        String responseString = operation.getResponseString();
        if (responseCode == BaseNetworkService.SUCCESS && responseString != null) {
            try {
                switch (WSUrl.matchUrl(operation.getUrl())) {
                    case WSUrl.VALIDATE_TASK_ID:
                        break;
                    default:
                        break;
                }
            } catch (JsonSyntaxException e) {
                L.e(TAG, e.toString(), e);
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
