package com.ros.smartrocket.db.entity.file;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class FileToUploadResponse extends BaseEntity {
    @SerializedName("FileCode")
    private String fileCode;
    @SerializedName("FileUrl")
    private String fileUrl;

    public String getFileCode() {
        return fileCode;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
