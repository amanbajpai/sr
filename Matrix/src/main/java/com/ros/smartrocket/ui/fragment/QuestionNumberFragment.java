package com.ros.smartrocket.ui.fragment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionNumberBL;

/**
 * Numeric question type
 */
public class QuestionNumberFragment extends BaseQuestionFragment {
    public QuestionNumberFragment() {
        super(new QuestionNumberBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_number;
    }
}