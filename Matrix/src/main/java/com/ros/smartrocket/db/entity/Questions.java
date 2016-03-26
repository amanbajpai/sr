package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Questions extends BaseEntity {
    private static final long serialVersionUID = 5410835468654163958L;

    @SerializedName("Questions")
    private Question[] questions;

    @SkipFieldInContentValues
    @SerializedName("MissionSize")
    private Integer missionSize;

    public Questions() {
    }

    public Question[] getQuestions() {
        return questions;
    }

    public void setQuestions(Question[] questions) {
        this.questions = questions;
    }

    public Integer getMissionSize() {
        return missionSize;
    }
}
