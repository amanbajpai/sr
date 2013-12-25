package com.ros.smartrocket.db.entity;

public class Answers extends BaseEntity {
    private static final long serialVersionUID = 5410835468654163958L;
    private Answer[] Answers;

    public Answers() {
    }

    public Answer[] getAnswers() {
        return Answers;
    }

    public void setAnswers(Answer[] Answers) {
        this.Answers = Answers;
    }
}
