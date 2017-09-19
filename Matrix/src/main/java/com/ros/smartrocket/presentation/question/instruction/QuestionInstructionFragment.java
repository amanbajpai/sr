package com.ros.smartrocket.presentation.question.instruction;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;


public class QuestionInstructionFragment extends BaseQuestionFragment {
    public QuestionInstructionFragment() {
        super(new QuestionInstructionBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_instruction_or_quit;
    }
}