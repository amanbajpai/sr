package com.ros.smartrocket.presentation.question.choose;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

import java.util.List;

public interface ChoiceMvpPresenter<V extends ChoiceMvpView>  extends BaseQuestionMvpPresenter<V>{
    void refreshNextButton(List<Answer> answers);
}
