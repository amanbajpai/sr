package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Questions extends BaseEntity {
    private static final long serialVersionUID = 5410835468654163958L;

    @SerializedName("Questions")
    private Question[] questions;

    public Questions() {
    }

    public Question[] getQuestions() {
        return questions;
    }
}
