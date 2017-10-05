package com.ros.smartrocket.presentation.question.instruction;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;


public class QuestionInstructionFragment extends BaseQuestionFragment<InstructionMvpPresenter<InstructionView>, InstructionView> {
    private InstructionMvpPresenter<InstructionView> presenter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_instruction_or_quit;
    }

    @Override
    public InstructionMvpPresenter<InstructionView> getPresenter() {
        if (presenter== null)
            presenter = new InstructionPresenter<>(question);
        return presenter;
    }

    @Override
    public InstructionView getMvpView() {
        return null;
    }
}