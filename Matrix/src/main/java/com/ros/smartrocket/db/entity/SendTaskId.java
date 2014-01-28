package com.ros.smartrocket.db.entity;

public class SendTaskId extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private Integer TaskId;
    private Double Longitude;
    private Double Latitude;

    public Integer getTaskId() {
        return TaskId;
    }

    public void setTaskId(Integer taskId) {
        TaskId = taskId;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }
}
