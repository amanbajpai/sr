package com.ros.smartrocket.ui.fragment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionInstructionBL;

/**
 * Instruction question type
 */
public class QuestionInstructionFragment extends BaseQuestionFragment {
    public QuestionInstructionFragment() {
        super(new QuestionInstructionBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_instruction_or_quit;
    }
}