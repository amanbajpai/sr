package com.ros.smartrocket.presentation.question.choose;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;

import java.util.List;

public class ChoicePresenter<V extends ChoiceMvpView> extends BaseQuestionPresenter<V> implements ChoiceMvpPresenter<V> {

    public ChoicePresenter(Question question) {
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
