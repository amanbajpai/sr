package com.ros.smartrocket.presentation.question.audit.additional;

import com.ros.smartrocket.db.entity.question.Answer;

public class TickCrossAnswerPair {
    private Answer tickAnswer;
    private Answer crossAnswer;

    public Answer getTickAnswer() {
        return tickAnswer;
    }

    public Answer getCrossAnswer() {
        return crossAnswer;
    }

    public void setTickAnswer(Answer tickAnswer) {
        this.tickAnswer = tickAnswer;
    }

    public void setCrossAnswer(Answer crossAnswer) {
        this.crossAnswer = crossAnswer;
    }
}
