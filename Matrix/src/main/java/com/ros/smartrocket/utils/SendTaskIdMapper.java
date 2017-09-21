package com.ros.smartrocket.utils;

import android.location.Location;

import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.SendTaskId;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.WaitingUploadTask;

public final class SendTaskIdMapper {

    private SendTaskIdMapper() {
    }

    public static SendTaskId getSendTaskIdForValidation(Task task, String cityName) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(task.getId());
        sendTaskId.setWaveId(task.getWaveId());
        sendTaskId.setMissionId(task.getMissionId());
        sendTaskId.setLatitude(task.getLatitude());
        sendTaskId.setLongitude(task.getLongitude());
        sendTaskId.setCityName(cityName);
        return sendTaskId;
    }

    public static SendTaskId getSendTaskIdForClaim(Task task, Location location) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(task.getId());
        sendTaskId.setLatitude(location.getLatitude());
        sendTaskId.setLongitude(location.getLongitude());
        return sendTaskId;
    }

    public static SendTaskId getSendTaskIdForStart(Task task) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setWaveId(task.getWaveId());
        sendTaskId.setTaskId(task.getId());
        sendTaskId.setMissionId(task.getMissionId());
        return sendTaskId;
    }

    public static SendTaskId getSendTaskIdForUnClaim(Task task) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(task.getId());
        sendTaskId.setMissionId(task.getMissionId());
        return sendTaskId;
    }

    public static SendTaskId getSendTaskIdForValidation(NotUploadedFile file, Location location) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(file.getId());
        sendTaskId.setWaveId(file.getWaveId());
        sendTaskId.setMissionId(file.getMissionId());
        sendTaskId.setLatitude(location.getLatitude());
        sendTaskId.setLongitude(location.getLongitude());
        return sendTaskId;
    }

    public static SendTaskId getSendTaskIdForValidation(WaitingUploadTask task, Location location) {
        SendTaskId sendTaskId = new SendTaskId();
        sendTaskId.setTaskId(task.getId());
        sendTaskId.setWaveId(task.getWaveId());
        sendTaskId.setMissionId(task.getMissionId());
        sendTaskId.setLatitude(location.getLatitude());
        sendTaskId.setLongitude(location.getLongitude());
        return sendTaskId;
    }


}
