package com.ros.smartrocket.presentation.question.instruction;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

interface InstructionMvpPresenter<V extends InstructionMvpView> extends BaseQuestionMvpPresenter<V> {
    void showInstructions();
}
