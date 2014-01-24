package com.ros.smartrocket.net;

import android.content.Intent;
import android.net.Uri;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.utils.L;
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

    public UploadFileNetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BaseOperation operation = (BaseOperation) intent.getSerializableExtra(KEY_OPERATION);
        if (operation != null) {
            NotUploadedFile notUploadedFile = (NotUploadedFile) operation.getEntities().get(0);

            //Separate main file
            File[] files = separateFile(notUploadedFile);
            if (files != null) {
                try {
                    for (int i = 0; i < files.length; i++) {

                        BaseOperation tempOperation = getSendTempFileOperation(files[i], notUploadedFile,
                                i == files.length - 1);
                        executeRequest(tempOperation);

                        int responseCode = operation.getResponseStatusCode();
                        String responseString = operation.getResponseString();
                        if (responseCode == 200 && responseString != null) {

                            L.i(TAG, "Upload temp file success: " + files[i].getName());

                            notUploadedFile.setPortion(notUploadedFile.getPortion() + 1);
                            notUploadedFile.setFileCode(new JSONObject(responseString).getString("filecode"));

                            FilesBL.updatePortionAndFileCode(notUploadedFile.getId(), notUploadedFile.getPortion(),
                                    notUploadedFile.getFileCode());

                            files[i].delete();
                        } else {
                            operation.setResponseStatusCode(NO_INTERNET);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            notifyOperationFinished(operation);
        }
    }

    public BaseOperation getSendTempFileOperation(File file, NotUploadedFile notUploadedFile, boolean isLastPortion) {
        AttachmentRequestEntity attachmentEntity = new AttachmentRequestEntity();
        attachmentEntity.setAttachmentPath(file.getPath());

        String taskId = String.valueOf(notUploadedFile.getTaskId());
        String questionId = String.valueOf(notUploadedFile.getQuestionId());
        String portion = String.valueOf(notUploadedFile.getPortion());
        String isLastPortionString = String.valueOf(isLastPortion);
        String fileCode = notUploadedFile.getFileCode();
        String fileName = notUploadedFile.getFileName();

        BaseOperation operation = new BaseOperation();
        operation.setUrl(WSUrl.UPLOAD_TASK_FILE, taskId, questionId, portion, isLastPortionString, fileCode, fileName);
        operation.setTag(Keys.UPLOAD_TASK_TEMP_FILE_OPERATION_TAG);
        operation.setMethod(BaseOperation.Method.POST);
        operation.getEntities().add(attachmentEntity);
        return operation;
    }

    public File[] separateFile(NotUploadedFile notUploadedFile) {
        try {
            File sourceFile = new File(Uri.parse(notUploadedFile.getFileUri()).getPath());
            byte[] soureByteArray = FileUtils.readFileToByteArray(sourceFile);

            int portainCount = (int) Math.ceil(soureByteArray.length / MAX_BYTE_SIZE);

            File[] files = new File[portainCount];

            for (int i = notUploadedFile.getPortion(); i < portainCount; i++) {

                byte[] tempByteArray = new byte[MAX_BYTE_SIZE];
                int startPosition = MAX_BYTE_SIZE * i;
                int length = MAX_BYTE_SIZE;

                if (startPosition + length > soureByteArray.length) {
                    length = soureByteArray.length - startPosition;
                }

                System.arraycopy(soureByteArray, startPosition, tempByteArray, 0, length);

                File tempFile = SelectImageManager.getTempFile(this);

                try {
                    FileOutputStream fos = new FileOutputStream(tempFile, false);
                    fos.write(tempByteArray);
                    fos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                files[i] = tempFile;
            }

            return files;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

    }
}
