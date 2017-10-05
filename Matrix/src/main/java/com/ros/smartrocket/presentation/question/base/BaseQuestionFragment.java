package com.ros.smartrocket.presentation.question.base;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.utils.LocaleUtils;

public abstract class BaseQuestionFragment<P extends BaseQuestionMvpPresenter<V>, V extends BaseQuestionMvpView> extends BaseFragment {
    protected Question question;
    protected P presenter;
    protected V view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ViewGroup view = (ViewGroup) localInflater.inflate(getLayoutResId(), null);
        question = (Question) getArguments().getSerializable(Keys.QUESTION);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.attachView(view);
        LocaleUtils.setCurrentLanguage();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.detachView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) view.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public abstract P getPresenter();

    public abstract V getMvpView();


    public boolean saveQuestion() {
        return presenter.saveQuestion();
    }

    public Question getQuestion() {
        return presenter.getQuestion();
    }

    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        //questionBL.setAnswerSelectedListener(answerSelectedListener);
    }

    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener listener) {
        //questionBL.setAnswerPageLoadingFinishedListener(listener);
    }

    public abstract int getLayoutResId();
}
