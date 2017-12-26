package com.ros.smartrocket.db.entity.task;

import com.google.gson.annotations.SerializedName;

public class SendTaskId extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("TaskId")
    private Integer taskId;
    @SerializedName("WaveId")
    private Integer waveId;
    @SerializedName("MissionId")
    private Integer missionId;
    @SerializedName("Longitude")
    private Double longitude;
    @SerializedName("Latitude")
    private Double latitude;
    @SerializedName("CityName")
    private String cityName;

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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getWaveId() {
        return waveId;
    }

    public void setWaveId(Integer waveId) {
        this.waveId = waveId;
    }
}
