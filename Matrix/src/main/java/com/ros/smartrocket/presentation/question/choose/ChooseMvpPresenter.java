package com.ros.smartrocket.presentation.question.choose;

import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

import java.util.List;

public interface ChooseMvpPresenter<V extends ChooseMvpView>  extends BaseQuestionMvpPresenter<V>{
    void refreshNextButton(List<Answer> answers);
}
