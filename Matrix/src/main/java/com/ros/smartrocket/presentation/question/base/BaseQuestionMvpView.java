package com.ros.smartrocket.presentation.question.base;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.base.MvpView;

public interface BaseQuestionMvpView extends MvpView {
    void validateView(Question question);

    void configureView(Question question);

    void answersDeleteComplete();

    void answersUpdate();

    void fillViewWithAnswers(Answer[] answers);

    void onDestroy();

}
