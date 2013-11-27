package com.matrix.db.entity;

public class Surveys extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;
    private Survey[] Surveys;

    public Surveys() {
    }

    public Survey[] getSurveys() {
        return Surveys;
    }

    public void setSurveys(Survey[] Surveys) {
        this.Surveys = Surveys;
    }
}
