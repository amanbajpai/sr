package com.ros.smartrocket.db.entity;

import android.database.Cursor;
import com.ros.smartrocket.db.WaitingUploadTaskDbSchema;
import com.ros.smartrocket.utils.L;

import java.util.Calendar;

/**
 * Data model of WaitingValidationTask entity
 */
public class WaitingUploadTask extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Integer taskId;
    private Integer missionId;
    private Integer waveId;
    private Long addedToUploadDateTime;
    private String cityName;
    private Double longitudeToValidation;
    private Double latitudeToValidation;
    private Boolean allFileSent;
    private Integer filesCount;

    public WaitingUploadTask() {
    }

    public WaitingUploadTask(Task task, int filesCount) {
        this.taskId = task.getId();
        this.missionId = task.getMissionId();
        this.waveId = task.getWaveId();
        this.addedToUploadDateTime = Calendar.getInstance().getTimeInMillis();
        this.cityName = task.getLocationName();
        this.longitudeToValidation = task.getLongitudeToValidation();
        this.latitudeToValidation = task.getLatitudeToValidation();
        this.allFileSent = false;
        this.filesCount = filesCount;
    }

    public static WaitingUploadTask fromCursor(Cursor c) {
        WaitingUploadTask result = new WaitingUploadTask();
        if (c.getCount() > 0) {
            result.set_id(c.getInt(WaitingUploadTaskDbSchema.Query._ID));
            result.setId(c.getInt(WaitingUploadTaskDbSchema.Query.ID));
            result.setTaskId(c.getInt(WaitingUploadTaskDbSchema.Query.TASK_ID));
            result.setWaveId(c.getInt(WaitingUploadTaskDbSchema.Query.WAVE_ID));
            result.setMissionId(c.getInt(WaitingUploadTaskDbSchema.Query.MISSION_ID));
            result.setAddedToUploadDateTime(c.getLong(WaitingUploadTaskDbSchema.Query.ADDED_TO_UPLOAD_DATE_TIME));
            result.setCityName(c.getString(WaitingUploadTaskDbSchema.Query.CITY_NAME));

            result.setLatitudeToValidation(c.getDouble(WaitingUploadTaskDbSchema.Query.LATITUDE_TO_VALIDATION));
            result.setLongitudeToValidation(c.getDouble(WaitingUploadTaskDbSchema.Query.LONGITUDE_TO_VALIDATION));

            result.setAllFileSent(c.getInt(WaitingUploadTaskDbSchema.Query.ALL_FILE_SENT) == 1);
            result.setFilesCount(c.getInt(WaitingUploadTaskDbSchema.Query.FILES_COUNT));

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

    public Integer getWaveId() {
        return waveId;
    }

    public void setWaveId(Integer waveId) {
        this.waveId = waveId;
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

    public int getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }
}
