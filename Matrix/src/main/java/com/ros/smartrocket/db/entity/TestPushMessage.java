package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class TestPushMessage extends BaseEntity {

    @SerializedName("StatusType")
    private Integer statusType;
    @SerializedName("TaskId")
    private Integer taskId;

    public Integer getStatusType() {
        return statusType;
    }

    public void setStatusType(Integer statusType) {
        this.statusType = statusType;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
