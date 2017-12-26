package com.ros.smartrocket.db.entity.task;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.db.entity.Warning;
import com.ros.smartrocket.db.entity.question.Questions;

public class ClaimTaskResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("MissionId")
    private Integer missionId;
    @SerializedName("Warnings")
    private Warning[] warnings;
    @SerializedName("Questionnaires")
    private Questions questions;


    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }

    public Warning[] getWarnings() {
        return warnings;
    }

    public void setWarnings(Warning[] warnings) {
        this.warnings = warnings;
    }

    public Questions getQuestions() {
        return questions;
    }
}
