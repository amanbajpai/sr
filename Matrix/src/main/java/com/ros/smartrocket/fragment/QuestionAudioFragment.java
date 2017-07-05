package com.ros.smartrocket.fragment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionAudioBL;

public class QuestionAudioFragment extends BaseQuestionFragment {

    public QuestionAudioFragment() {
        super(new QuestionAudioBL());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_audio;
    }
}
