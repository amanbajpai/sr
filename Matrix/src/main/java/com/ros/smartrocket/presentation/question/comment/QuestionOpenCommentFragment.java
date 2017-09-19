package com.ros.smartrocket.presentation.question.comment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

public class QuestionOpenCommentFragment extends BaseQuestionFragment {
    public QuestionOpenCommentFragment() {
        super(new QuestionOpenCommentBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_open_comment;
    }
}