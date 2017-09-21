package com.ros.smartrocket.presentation.question.quit;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;
import com.ros.smartrocket.presentation.question.instruction.QuestionInstructionBL;

public class QuestionQuitStatementFragment extends BaseQuestionFragment {
    public QuestionQuitStatementFragment() {
        super(new QuestionInstructionBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_instruction_or_quit;
    }
}