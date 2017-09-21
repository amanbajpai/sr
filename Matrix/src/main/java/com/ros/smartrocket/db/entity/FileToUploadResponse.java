package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class FileToUploadResponse extends BaseEntity {
    @SerializedName("FileCode")
    String fileCode;

    public String getFileCode() {
        return fileCode;
    }
}
