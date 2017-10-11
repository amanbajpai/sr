package com.ros.smartrocket.presentation.question.number;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;
import com.ros.smartrocket.utils.L;

public class NumberPresenter<V extends NumberMvpView> extends BaseQuestionPresenter<V> implements NumberMvpPresenter<V> {

    public NumberPresenter(Question question) {
        super(question);
    }

    @Override
    public void onNumberEntered(String number) {
        Double answerNumber = null;
        try {
            answerNumber = Double.valueOf(number.trim());
        } catch (NumberFormatException e) {
            L.d("Parse", "Not a Double " + number);
        }
        boolean selected = answerNumber != null
                && answerNumber >= question.getMinValue()
                && answerNumber <= question.getMaxValue();
        refreshNextButton(selected);
    }

    @Override
    public boolean saveQuestion() {
        if (question != null) question.setFirstAnswer(getMvpView().getAnswerValue());
        return super.saveQuestion();
    }
}
