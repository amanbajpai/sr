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

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseQuestionFragment<P extends BaseQuestionMvpPresenter<V>, V extends BaseQuestionMvpView> extends BaseFragment {
    protected Question question;
    protected P presenter;
    protected V mvpView;
    private boolean isPreview;
    private boolean isRedo;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ViewGroup view = (ViewGroup) localInflater.inflate(getLayoutResId(), null);
        unbinder = ButterKnife.bind(this, view);
        handleArgs();
        initPresenter();
        return view;
    }

    private void initPresenter() {
        presenter = getPresenter();
        presenter.setPreview(isPreview);
        presenter.setRedo(isRedo);
        presenter.setAnswerPageLoadingFinishedListener(answerPageLoadingFinishedListener);
        presenter.setAnswerSelectedListener(answerSelectedListener);
        mvpView = getMvpView();
    }

    protected void handleArgs() {
        Bundle args = getArguments();
        if (args != null) {
            question = (Question) args.getSerializable(Keys.QUESTION);
            isPreview = args.getBoolean(Keys.IS_PREVIEW);
            isRedo = args.getBoolean(Keys.IS_REDO);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.attachView(mvpView);
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
        if (mvpView != null) mvpView.onDestroy();
        unbinder.unbind();
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
        this.answerSelectedListener = answerSelectedListener;
    }

    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener) {
        this.answerPageLoadingFinishedListener = answerPageLoadingFinishedListener;
    }

    public abstract int getLayoutResId();
}
