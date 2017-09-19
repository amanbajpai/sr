package com.ros.smartrocket.presentation.question.photo;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

public class QuestionPhotoFragment extends BaseQuestionFragment {
    public QuestionPhotoFragment() {
        super(new QuestionPhotoBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_photo;
    }
}
