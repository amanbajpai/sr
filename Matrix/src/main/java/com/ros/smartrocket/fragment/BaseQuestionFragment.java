package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionBaseBL;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Fragment for display About information
 */
public abstract class BaseQuestionFragment extends Fragment {
    protected QuestionBaseBL questionBL;
    protected AsyncQueryHandler handler;

    public abstract int getLayoutResId();

    public BaseQuestionFragment(QuestionBaseBL questionBL) {
        super();
        this.questionBL = questionBL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ViewGroup view = (ViewGroup) localInflater.inflate(getLayoutResId(), null);

        Question question = (Question) getArguments().getSerializable(Keys.QUESTION);
        questionBL.initView(view, question, savedInstanceState);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        questionBL.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    public boolean saveQuestion() {
        return questionBL.saveQuestion(handler);
    }

    public void clearAnswer() {
        questionBL.clearAnswer(handler);
    }

    public Question getQuestion() {
        return questionBL.getQuestion();
    }

    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        questionBL.setAnswerSelectedListener(answerSelectedListener);
    }

    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener listener) {
        questionBL.setAnswerPageLoadingFinishedListener(listener);
    }
}
