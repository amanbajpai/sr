package com.ros.smartrocket.presentation.question.choose.single;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

public class QuestionSingleChooseFragment extends BaseQuestionFragment {
    public QuestionSingleChooseFragment() {
        super(new QuestionSingleChooseBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_choose;
    }
}