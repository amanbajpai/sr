package com.ros.smartrocket.presentation.question.instruction;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

import java.util.ArrayList;

interface InstructionMvpPresenter<V extends InstructionMvpView> extends BaseQuestionMvpPresenter<V> {
    void showInstructions();

    ArrayList<String> getDialogGalleryImages();

}
