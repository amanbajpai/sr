package com.ros.smartrocket.db.entity.file;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.NotUploadedFileDbSchema;
import com.ros.smartrocket.utils.L;

public class NotUploadedFile extends BaseNotUploadedFile {
    @SerializedName("MissionId")
    private Integer missionId;
    @SerializedName("WaveId")
    private Integer waveId;
    @SerializedName("TaskId")
    private Integer taskId;
    @SerializedName("TaskName")
    private String taskName;
    @SerializedName("QuestionId")
    private Integer questionId;
    @SerializedName("LongitudeToValidation")
    private Double longitudeToValidation;
    @SerializedName("LatitudeToValidation")
    private Double latitudeToValidation;

    private Boolean use3G;
    private Integer showNotificationStepId;
    private Boolean taskValidated;

    public NotUploadedFile() {
    }

    public static NotUploadedFile fromCursor(Cursor c) {
        NotUploadedFile result = new NotUploadedFile();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(NotUploadedFileDbSchema.Query._ID));
            result.setId(c.getInt(NotUploadedFileDbSchema.Query.ID));
            result.setTaskId(c.getInt(NotUploadedFileDbSchema.Query.TASK_ID));
            result.setWaveId(c.getInt(NotUploadedFileDbSchema.Query.WAVE_ID));
            result.setMissionId(c.getInt(NotUploadedFileDbSchema.Query.MISSION_ID));
            result.setQuestionId(c.getInt(NotUploadedFileDbSchema.Query.QUESTION_ID));
            result.setFileUri(c.getString(NotUploadedFileDbSchema.Query.FILE_URI));
            result.setAddedToUploadDateTime(c.getLong(NotUploadedFileDbSchema.Query.ADDED_TO_UPLOAD_DATE_TIME));
            result.setEndDateTime(c.getLong(NotUploadedFileDbSchema.Query.END_DATE_TIME));
            result.setUse3G(c.getInt(NotUploadedFileDbSchema.Query.USE_3G) != 0);
            result.setFileSizeB(c.getLong(NotUploadedFileDbSchema.Query.FILE_SIZE_B));
            result.setShowNotificationStepId(c.getInt(NotUploadedFileDbSchema.Query.SHOW_NOTIFICATION_STEP_ID));

            result.setPortion(c.getInt(NotUploadedFileDbSchema.Query.PORTION));
            result.setFileCode(c.getString(NotUploadedFileDbSchema.Query.FILE_CODE));
            result.setFileName(c.getString(NotUploadedFileDbSchema.Query.FILE_NAME));

            result.setTaskName(c.getString(NotUploadedFileDbSchema.Query.TASK_NAME));

            result.setLatitudeToValidation(c.getDouble(NotUploadedFileDbSchema.Query.LATITUDE_TO_VALIDATION));
            result.setLongitudeToValidation(c.getDouble(NotUploadedFileDbSchema.Query.LONGITUDE_TO_VALIDATION));

            result.setTaskValidated(c.getInt(NotUploadedFileDbSchema.Query.TASK_VALIDATED) == 1);
            result.setFileId(c.getString(NotUploadedFileDbSchema.Query.FILE_ID));
        }
        L.d("NotUploadedFile", result.toString());
        return result;
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

    public Boolean getUse3G() {
        return use3G;
    }

    public void setUse3G(Boolean use3G) {
        this.use3G = use3G;
    }

    public Integer getShowNotificationStepId() {
        return showNotificationStepId;
    }

    public void setShowNotificationStepId(Integer showNotificationStepId) {
        this.showNotificationStepId = showNotificationStepId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Double getLongitudeToValidation() {
        return longitudeToValidation == null ? 0 : longitudeToValidation;
    }

    public void setLongitudeToValidation(Double longitudeToValidation) {
        this.longitudeToValidation = longitudeToValidation;
    }

    public Double getLatitudeToValidation() {
        return latitudeToValidation == null ? 0 : latitudeToValidation;
    }

    public void setLatitudeToValidation(Double latitudeToValidation) {
        this.latitudeToValidation = latitudeToValidation;
    }

    public Boolean getTaskValidated() {
        return taskValidated;
    }

    public void setTaskValidated(Boolean taskValidated) {
        this.taskValidated = taskValidated;
    }

    public Integer getWaveId() {
        return waveId;
    }

    public void setWaveId(Integer waveId) {
        this.waveId = waveId;
    }

    @Override
    public String getFileId() {
        return taskId != null ? taskId.toString() : "0";
    }
}
