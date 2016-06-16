package com.ros.smartrocket.db.entity;

import com.ros.smartrocket.db.entity.WaitingUploadTask;

/**
 * Used for update files upload progress
 */

public final class ProgressUpdate {
    private Integer taskId;
    private Integer missionId;
    private Integer waveId;
    private Integer totalFilesCount;
    private Integer uploadedFilesCount;

    public ProgressUpdate(WaitingUploadTask task, Integer uploadedCount){
        taskId = task.getTaskId();
        missionId = task.getMissionId();
        waveId = task.getWaveId();
        totalFilesCount = task.getFilesCount();
        uploadedFilesCount = uploadedCount;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public Integer getMissionId() {
        return missionId;
    }

    public Integer getWaveId() {
        return waveId;
    }

    public Integer getTotalFilesCount() {
        return totalFilesCount;
    }

    public Integer getUploadedFilesCount() {
        return uploadedFilesCount;
    }

}
