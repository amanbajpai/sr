package com.ros.smartrocket.presentation.question.instruction;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

import butterknife.BindView;
import butterknife.Unbinder;


public class QuestionInstructionFragment extends BaseQuestionFragment<InstructionMvpPresenter<InstructionMvpView>, InstructionMvpView> {
    @BindView(R.id.instructionView)
    InstructionView instructionView;
    Unbinder unbinder;
    private InstructionMvpPresenter<InstructionMvpView> presenter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_instruction_or_quit;
    }

    @Override
    public InstructionMvpPresenter<InstructionMvpView> getPresenter() {
        if (presenter == null)
            presenter = new InstructionPresenter<>(question);
        return presenter;
    }

    @Override
    public InstructionMvpView getMvpView() {
        return instructionView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}