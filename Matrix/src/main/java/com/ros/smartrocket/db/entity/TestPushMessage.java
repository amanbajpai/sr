package com.ros.smartrocket.db.entity;

public class TestPushMessage extends BaseEntity {

    private Integer StatusType;
    private Integer TaskId;

    public Integer getStatusType() {
        return StatusType;
    }

    public void setStatusType(Integer statusType) {
        StatusType = statusType;
    }

    public Integer getTaskId() {
        return TaskId;
    }

    public void setTaskId(Integer taskId) {
        TaskId = taskId;
    }
}
