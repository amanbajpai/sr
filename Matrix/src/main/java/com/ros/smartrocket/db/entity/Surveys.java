package com.ros.smartrocket.db.entity;

public class Surveys extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;
    private Survey[] Surveys;

    public Surveys() {
    }

    public Survey[] getSurveys() {
        return Surveys;
    }

    public void setSurveys(Survey[] surveys) {
        this.Surveys = surveys;
    }
}
