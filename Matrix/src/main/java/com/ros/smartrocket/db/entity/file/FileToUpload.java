package com.ros.smartrocket.db.entity.file;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class FileToUpload extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    @SerializedName("FileCode")
    private String fileCode;
    @SerializedName("Filename")
    private String filename;
    @SerializedName("FileLength")
    private Long fileLength;
    @SerializedName("FileOffset")
    private Long fileOffset;
    @SerializedName("LanguageCode")
    private String languageCode;
    @SerializedName("ChunkSize")
    private Long chunkSize;

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }

    public Long getFileOffset() {
        return fileOffset;
    }

    public void setFileOffset(Long fileOffset) {
        this.fileOffset = fileOffset;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }
}
