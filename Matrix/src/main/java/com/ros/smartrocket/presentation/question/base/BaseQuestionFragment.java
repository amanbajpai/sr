package com.ros.smartrocket.presentation.question.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.utils.LocaleUtils;

public abstract class BaseQuestionFragment extends BaseFragment {
    protected QuestionBaseBL questionBL;

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
        questionBL.initView(view, question, savedInstanceState, (BaseActivity) getActivity(), this, null);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        questionBL.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocaleUtils.setCurrentLanguage();
        questionBL.onStart();
    }

    @Override
    public void onStop() {
        questionBL.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        questionBL.destroyView();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        questionBL.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LocaleUtils.setCurrentLanguage();
        if (!questionBL.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean saveQuestion() {
        return questionBL.saveQuestion();
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
