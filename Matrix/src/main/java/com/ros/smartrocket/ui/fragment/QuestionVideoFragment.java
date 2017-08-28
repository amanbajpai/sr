package com.ros.smartrocket.ui.fragment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionVideoBL;

/**
 * Video question type
 */
public class QuestionVideoFragment extends BaseQuestionFragment  {
    public QuestionVideoFragment() {
        super(new QuestionVideoBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_video;
    }
}
