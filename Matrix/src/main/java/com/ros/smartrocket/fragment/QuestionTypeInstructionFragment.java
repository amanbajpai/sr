package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionTypeInstructionBL;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Instruction question type
 */
public class QuestionTypeInstructionFragment extends BaseQuestionFragment {
    private QuestionTypeInstructionBL questionBL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_8, null);

        if (getArguments() != null) {
            Question question = (Question) getArguments().getSerializable(Keys.QUESTION);
            questionBL = new QuestionTypeInstructionBL(view, question);
        }

        return view;
    }

    @Override
    public boolean saveQuestion() {
        return questionBL.saveQuestion();
    }

    @Override
    public void clearAnswer() {
        questionBL.clearAnswer();
    }

    @Override
    public Question getQuestion() {
        return questionBL.getQuestion();
    }

    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        questionBL.setAnswerSelectedListener(answerSelectedListener);
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener
                                                             answerPageLoadingFinishedListener) {
        questionBL.setAnswerPageLoadingFinishedListener(answerPageLoadingFinishedListener);
    }
}
