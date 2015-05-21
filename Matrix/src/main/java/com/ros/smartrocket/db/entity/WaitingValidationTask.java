package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.WaitingValidationTaskDbSchema;
import com.ros.smartrocket.utils.L;

/**
 * Data model of WaitingValidationTask entity
 */
public class WaitingValidationTask extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Integer taskId;
    private Integer missionId;
    private Integer questionId;
    private Long addedToUploadDateTime;
    private String cityName;
    private Double longitudeToValidation;
    private Double latitudeToValidation;
    private Boolean allFileSent;

    public WaitingValidationTask() {
    }

    public static WaitingValidationTask fromCursor(Cursor c) {
        WaitingValidationTask result = new WaitingValidationTask();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(WaitingValidationTaskDbSchema.Query._ID));
            result.setId(c.getInt(WaitingValidationTaskDbSchema.Query.ID));
            result.setTaskId(c.getInt(WaitingValidationTaskDbSchema.Query.TASK_ID));
            result.setQuestionId(c.getInt(WaitingValidationTaskDbSchema.Query.QUESTION_ID));
            result.setMissionId(c.getInt(WaitingValidationTaskDbSchema.Query.MISSION_ID));
            result.setAddedToUploadDateTime(c.getLong(WaitingValidationTaskDbSchema.Query.ADDED_TO_UPLOAD_DATE_TIME));
            result.setCityName(c.getString(WaitingValidationTaskDbSchema.Query.CITY_NAME));

            result.setLatitudeToValidation(c.getDouble(WaitingValidationTaskDbSchema.Query.LATITUDE_TO_VALIDATION));
            result.setLongitudeToValidation(c.getDouble(WaitingValidationTaskDbSchema.Query.LONGITUDE_TO_VALIDATION));

            result.setAllFileSent(c.getInt(WaitingValidationTaskDbSchema.Query.ALL_FILE_SENT) == 1);

        }
        L.d("WaitingValidationTask", result.toString());
        return result;
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

    public Long getAddedToUploadDateTime() {
        return addedToUploadDateTime;
    }

    public void setAddedToUploadDateTime(Long addedToUploadDateTime) {
        this.addedToUploadDateTime = addedToUploadDateTime;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }

    public Boolean getAllFileSent() {
        return allFileSent;
    }

    public void setAllFileSent(Boolean allFileSent) {
        this.allFileSent = allFileSent;
    }
}
