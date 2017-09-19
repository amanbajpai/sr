package com.ros.smartrocket.presentation.question.video;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

public class QuestionVideoFragment extends BaseQuestionFragment {
    public QuestionVideoFragment() {
        super(new QuestionVideoBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_video;
    }
}
