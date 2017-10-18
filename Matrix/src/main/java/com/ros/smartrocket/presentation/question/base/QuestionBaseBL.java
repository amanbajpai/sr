package com.ros.smartrocket.presentation.question.base;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.presentation.question.main.QuestionsActivity;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionBaseBL {
    protected OnAnswerSelectedListener answerSelectedListener;
    protected OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    protected AsyncQueryHandler handler;
    protected Question question;
    protected Bundle savedInstanceState;
    protected BaseActivity activity;
    protected Fragment fragment;
    protected View view;
    protected Product product;
    @BindView(R.id.presetValidationComment)
    TextView presetValidationComment;
    @BindView(R.id.validationComment)
    TextView validationComment;

    protected TextView questionText;

    public final void initView(View view, Question question, Bundle savedInstanceState, BaseActivity activity,
                               BaseFragment fragment, Product product) {
        ButterKnife.bind(this, view);
        this.savedInstanceState = savedInstanceState;
        this.product = product;
        this.question = question;
        this.activity = activity;
        this.fragment = fragment;
        this.view = view;
        this.handler = new BaseDbHandler(activity.getContentResolver());
        configureView();
        validateView();
    }

    protected void validateView() {
        questionText = (TextView) view.findViewById(R.id.questionText);
    }

    protected void configureView() {
        // Do nothing
    }

    public void destroyView() {
        handler.removeCallbacksAndMessages(null);
    }

    public void onStart() {
        // Do nothing
    }

    public void onPause() {
        // Do nothing
    }

    public void onStop() {
        // Do nothing
    }

    public void loadAnswers() {
    }

    protected Integer getProductId() {
        return product != null ? product.getId() : null;
    }

    public Question getQuestion() {
        return question;
    }

    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener listener) {
        this.answerPageLoadingFinishedListener = listener;
    }

    public void onSaveInstanceState(Bundle outState) {
        // Do nothing
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        return false;
    }

    public boolean saveQuestion() {
        return true;
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null)
            answerSelectedListener.onAnswerSelected(true, question.getId());

        if (answerPageLoadingFinishedListener != null)
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
    }

    public FragmentActivity getActivity() {
        return activity;
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    class BaseDbHandler extends AsyncQueryHandler {
        BaseDbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == AnswerDbSchema.Query.TOKEN_QUERY) {
                Answer[] answers = null;
                fillViewWithAnswers(answers);
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if (token == AnswerDbSchema.Query.TOKEN_UPDATE) answersUpdate();
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if (token == AnswerDbSchema.Query.TOKEN_DELETE) answersDeleteComplete();
        }
    }

    protected Answer[] addEmptyAnswer(Answer[] currentAnswerArray) {
        Answer answer = new Answer();
        answer.setRandomId();
        answer.setQuestionId(question.getId());
        answer.setTaskId(question.getTaskId());
        answer.setMissionId(question.getMissionId());
        answer.setProductId(product != null ? product.getId() : 0);

        if (!isPreview()) {
            Uri uri = getActivity().getContentResolver().insert(AnswerDbSchema.CONTENT_URI, answer.toContentValues());
            long id = ContentUris.parseId(uri);
            answer.set_id(id);
        }

        Answer[] resultAnswerArray = Arrays.copyOf(currentAnswerArray, currentAnswerArray.length + 1);
        resultAnswerArray[currentAnswerArray.length] = answer;
        return resultAnswerArray;
    }

    protected boolean isPreview() {
        return getActivity() != null && getActivity() instanceof QuestionsActivity && ((QuestionsActivity) getActivity()).isPreview();
    }

    protected void answersDeleteComplete() {
        // Nothing to do
    }

    protected void answersUpdate() {
        // Nothing to do
    }

    protected void fillViewWithAnswers(Answer[] answers) {
        // Do nothing
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    protected void showLoading() {
        getActivity().runOnUiThread(() -> {
            if (activity != null)
                activity.hideLoading();
        });

    }

    protected void hideProgressDialog() {
        if (activity != null) activity.hideLoading();
    }
}