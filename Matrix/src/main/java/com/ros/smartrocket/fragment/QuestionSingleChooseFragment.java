package com.ros.smartrocket.fragment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionSingleChooseBL;

/**
 * Single choose question type
 */
public class QuestionSingleChooseFragment extends BaseQuestionFragment {
    public QuestionSingleChooseFragment() {
        super(new QuestionSingleChooseBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_choose;
    }
}