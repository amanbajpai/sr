package com.ros.smartrocket.presentation.question.number;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

public class QuestionNumberFragment extends BaseQuestionFragment {
    public QuestionNumberFragment() {
        super(new QuestionNumberBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_number;
    }
}