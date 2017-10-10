package com.ros.smartrocket.presentation.question.base;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.base.MvpPresenter;

import java.util.List;

public interface BaseQuestionMvpPresenter<V extends BaseQuestionMvpView> extends MvpPresenter<V> {
    boolean saveQuestion();

    Question getQuestion();

    void loadAnswers();

    List<Answer> addEmptyAnswer(List<Answer> currentAnswerArray);

}
