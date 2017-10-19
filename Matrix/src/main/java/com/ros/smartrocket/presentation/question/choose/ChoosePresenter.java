package com.ros.smartrocket.presentation.question.choose;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;

import java.util.List;

public class ChoosePresenter<V extends ChooseMvpView> extends BaseQuestionPresenter<V> implements ChooseMvpPresenter<V> {

    public ChoosePresenter(Question question) {
        super(question);
    }

    @Override
    public void refreshNextButton(List<Answer> answers) {
        question.setAnswers(answers);
        boolean selected = false;
        for (Answer answer : answers) {
            if (answer.getChecked()) {
                selected = true;
                break;
            }
        }
        refreshNextButton(selected);
    }
}
