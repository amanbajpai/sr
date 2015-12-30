package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class ClaimTaskResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("MissionId")
    private Integer missionId;

    @SerializedName("IsUpdateRequired")
    private boolean isUpdateRequired;

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }

    public boolean isUpdateRequired() {
        return isUpdateRequired;
    }

    public void setUpdateRequired(boolean isUpdateRequired) {
        this.isUpdateRequired = isUpdateRequired;
    }
}
