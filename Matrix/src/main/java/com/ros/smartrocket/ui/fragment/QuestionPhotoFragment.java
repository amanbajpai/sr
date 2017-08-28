package com.ros.smartrocket.ui.fragment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionPhotoBL;

/**
 * Multiple photo question type
 */
public class QuestionPhotoFragment extends BaseQuestionFragment {
    public QuestionPhotoFragment() {
        super(new QuestionPhotoBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_photo;
    }
}
