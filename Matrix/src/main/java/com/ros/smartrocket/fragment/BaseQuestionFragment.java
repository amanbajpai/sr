package com.ros.smartrocket.fragment;

import android.support.v4.app.Fragment;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Fragment for display About information
 */
public class BaseQuestionFragment extends Fragment {
    private static final String TAG = BaseQuestionFragment.class.getSimpleName();

    public void saveQuestion() {
    }

    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
    }

    public Question getQuestion() {
        return null;
    }
}
