package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class ClaimTaskResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("MissionId")
    private Integer missionId;

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }
}
