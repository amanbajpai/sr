package com.ros.smartrocket.fragment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionOpenCommentBL;

/**
 * Open comment question type
 */
public class QuestionOpenCommentFragment extends BaseQuestionFragment {
    public QuestionOpenCommentFragment() {
        super(new QuestionOpenCommentBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_open_comment;
    }
}