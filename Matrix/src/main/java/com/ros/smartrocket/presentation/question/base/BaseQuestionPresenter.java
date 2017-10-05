package com.ros.smartrocket.presentation.question.base;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.base.BasePresenter;

public class BaseQuestionPresenter<V extends BaseQuestionMvpView> extends BasePresenter<V> implements BaseQuestionMvpPresenter<V> {
    Question question;

    public BaseQuestionPresenter(Question question) {
        this.question = question;
    }

    @Override
    public boolean saveQuestion() {
        return false;
    }

    @Override
    public Question getQuestion() {
        return question;
    }
}
