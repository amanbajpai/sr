package com.ros.smartrocket.fragment;

import android.os.Bundle;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionInstructionBL;

/**
 * Instruction question type
 */
public class QuestionInstructionFragment extends BaseQuestionFragment {
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_instruction;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        questionBL = new QuestionInstructionBL();
    }
}