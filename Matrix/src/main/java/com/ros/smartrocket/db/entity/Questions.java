package com.ros.smartrocket.db.entity;

public class Questions extends BaseEntity {
    private static final long serialVersionUID = 5410835468654163958L;
    private Question[] Questions;

    public Questions() {
    }

    public Question[] getQuestions() {
        return Questions;
    }

    public void setQuestions(Question[] Questions) {
        this.Questions = Questions;
    }
}
