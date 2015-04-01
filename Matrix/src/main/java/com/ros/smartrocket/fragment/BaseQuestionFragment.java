package com.ros.smartrocket.fragment;

import android.support.v4.app.Fragment;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Fragment for display About information
 */
public class BaseQuestionFragment extends Fragment {
    public boolean saveQuestion() {
        return true;
    }

    public void clearAnswer() {
    }

    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
    }

    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener
                                                             answerPageLoadingFinishedListener) {
    }

    public Question getQuestion() {
        return null;
    }
}
