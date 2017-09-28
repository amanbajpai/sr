package com.ros.smartrocket.db.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class FileToUploadMultipart extends BaseEntity {
    @SkipFieldInContentValues
    private static final long serialVersionUID = 5410835468659163928L;
    @SerializedName("TaskId")
    private Integer taskId;
    @SerializedName("MissionId")
    private Integer missionId;
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
    @SerializedName("LanguageCode")
    private String languageCode;
    private transient byte[] fileBody;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
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

    public String getJson() {
        return new Gson().toJson(this);
    }

    public byte[] getFileBody() {
        return fileBody;
    }

    public void setFileBody(byte[] fileBody) {
        this.fileBody = fileBody;
    }
}
