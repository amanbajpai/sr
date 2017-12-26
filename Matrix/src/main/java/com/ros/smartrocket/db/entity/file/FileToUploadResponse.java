package com.ros.smartrocket.db.entity.file;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class FileToUploadResponse extends BaseEntity {
    @SerializedName("FileCode")
    String fileCode;

    public String getFileCode() {
        return fileCode;
    }
}
