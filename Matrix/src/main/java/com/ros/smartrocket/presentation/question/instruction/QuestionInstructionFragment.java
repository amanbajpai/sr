package com.ros.smartrocket.presentation.question.instruction;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

import butterknife.BindView;


public class QuestionInstructionFragment extends BaseQuestionFragment<InstructionMvpPresenter<InstructionMvpView>, InstructionMvpView> {
    @BindView(R.id.instructionView)
    InstructionView instructionView;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_instruction_or_quit;
    }

    @Override
    public InstructionMvpPresenter<InstructionMvpView> getPresenter() {
        return new InstructionPresenter<>(question);
    }

    @Override
    public InstructionMvpView getMvpView() {
        instructionView.setPresenter(presenter);
        return instructionView;
    }
}