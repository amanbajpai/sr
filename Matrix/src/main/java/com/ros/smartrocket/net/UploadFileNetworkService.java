package com.ros.smartrocket.net;

import android.content.Intent;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.FilesBL;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.db.entity.FileToUpload;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.ErrorResponse;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.image.SelectImageManager;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UploadFileNetworkService extends BaseNetworkService {
    private static final String TAG = "UploadFileNetworkService";
    public static final String TAG_RECRUITING = "recruiting";
    public static int MAX_BYTE_SIZE = 1000 * 16;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    public UploadFileNetworkService() {
        super("NetworkService");
        // TODO tododododod
//        for (int i = 0; i < files.length; i++) {
//            BaseOperation tempOperation = getSendTempFileOperation(files[i], notUploadedFile,
//                    mainFileLength);
//            int responseCode = tempOperation.getResponseStatusCode();
//            String responseString = tempOperation.getResponseString();
//            Integer responseErrorCode = tempOperation.getResponseErrorCode();
//            if (responseCode == BaseNetworkService.SUCCESS && responseString != null) {
//                notUploadedFile.setPortion(notUploadedFile.getPortion() + 1);
//                notUploadedFile.setFileCode(new JSONObject(responseString).getString("FileCode"));
//                FilesBL.updatePortionAndFileCode(notUploadedFile.getId(),
//                        notUploadedFile.getPortion(), notUploadedFile.getFileCode());
//                files.remove(i);
//            } else if (responseErrorCode != null
//                    && (responseErrorCode == BaseNetworkService.TASK_NOT_FOUND_ERROR_CODE ||
//                    responseErrorCode == BaseNetworkService.FILE_ALREADY_UPLOADED_ERROR_CODE ||
//                    responseErrorCode == BaseNetworkService.FILE_NOT_FOUND)) {
//                files.remove(i);
//                break;
//            } else {
//                cleanFiles(files);
//                break;
//            }
//        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


    protected String getRequestJson(BaseOperation operation) {
        return "";
    }

    protected void processResponse(BaseOperation operation) {
    }
}
