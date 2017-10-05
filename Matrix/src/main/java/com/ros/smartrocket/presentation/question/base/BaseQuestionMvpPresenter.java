package com.ros.smartrocket.presentation.question.base;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface BaseQuestionMvpPresenter<V extends BaseQuestionMvpView> extends MvpPresenter<V> {
    boolean saveQuestion();
    Question getQuestion();
}
