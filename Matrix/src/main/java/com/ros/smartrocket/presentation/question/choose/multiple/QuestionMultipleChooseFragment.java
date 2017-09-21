package com.ros.smartrocket.presentation.question.choose.multiple;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

public class QuestionMultipleChooseFragment extends BaseQuestionFragment {
    public QuestionMultipleChooseFragment() {
        super(new QuestionMultipleChooseBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_choose;
    }
}