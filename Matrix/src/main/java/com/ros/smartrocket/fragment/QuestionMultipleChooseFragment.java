package com.ros.smartrocket.fragment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionMultipleChooseBL;

/**
 * Multiple choose question type
 */
public class QuestionMultipleChooseFragment extends BaseQuestionFragment {
    public QuestionMultipleChooseFragment() {
        super(new QuestionMultipleChooseBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_choose;
    }
}