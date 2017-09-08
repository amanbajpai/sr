package com.ros.smartrocket.db.store;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;

public class ReDoQuestionStore extends QuestionStore {

    public ReDoQuestionStore(Task task) {
        super(task);
    }

    @Override
    protected Question prepareQuestion(int i, Question question) {
        Question q = super.prepareQuestion(i, question);
        question.setOrderId(i);
        return q;
    }
}
