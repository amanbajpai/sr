package com.ros.smartrocket.presentation.question.number;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

public interface NumberMvpPresenter<V extends NumberMvpView> extends BaseQuestionMvpPresenter<V> {
    void onNumberEntered(String number);
}
