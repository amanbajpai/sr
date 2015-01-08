package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Data model of NotUploadedFile entity
 */
public class FileToUpload extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    @SerializedName("TaskId")
    private Integer taskId;
    @SerializedName("QuestionId")
    private Integer questionId;
    @SerializedName("FileCode")
    private String fileCode;
    @SerializedName("Filename")
    private String filename;
    @SerializedName("FileLength")
    private Long fileLength;
    @SerializedName("FileOffset")
    private Long fileOffset;
    @SerializedName("FileBase64String")
    private String fileBase64String;
    @SerializedName("LanguageCode")
    private String languageCode;

    public FileToUpload() {
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

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

    public String getFileBase64String() {
        return fileBase64String;
    }

    public void setFileBase64String(String fileBase64String) {
        this.fileBase64String = fileBase64String;
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

}
