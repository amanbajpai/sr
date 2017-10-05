package com.ros.smartrocket.presentation.question.instruction;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;

class InstructionPresenter<V extends InstructionView> extends BaseQuestionPresenter<V> implements InstructionMvpPresenter<V> {

    public InstructionPresenter(Question question) {
        super(question);
    }
}
