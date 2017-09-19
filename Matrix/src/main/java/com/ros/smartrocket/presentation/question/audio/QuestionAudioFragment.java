package com.ros.smartrocket.presentation.question.audio;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

public class QuestionAudioFragment extends BaseQuestionFragment {

    public QuestionAudioFragment() {
        super(new QuestionAudioBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_audio;
    }
}
